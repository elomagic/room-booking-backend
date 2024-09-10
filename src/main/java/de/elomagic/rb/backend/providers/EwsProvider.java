package de.elomagic.rb.backend.providers;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendCancellationsMode;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

import de.elomagic.rb.backend.CommonRbException;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EwsProvider  implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EwsProvider.class);

    private static final UUID ORIGINAL_UID_PROPERTY_UID = UUID.fromString("84178b2e-50a0-46fa-85b9-d87fc5185886");

    private ExchangeService exchangeService;

    @Value("${rb.ext.ews.uri}")
    private String uri;
    @Value("${rb.ext.ews.autoDiscover}")
    private String autoDiscover;
    @Value("${rb.ext.ews.credentials.username}")
    private String username;
    @Value("${rb.ext.ews.credentials.password}")
    private String password;

    @PostConstruct
    public void startSession() throws Exception {
        initServiceClient();
    }

    public Set<AppointmentDTO> queryAppointments(@Nonnull String resourceAddress, @Nonnull ZonedDateTime start, @Nonnull ZonedDateTime end) {
        try {
            Date dStart = Date.from(start.toInstant());
            Date dEnd = Date.from(end.toInstant());

            FolderId folderId = new FolderId(WellKnownFolderName.Calendar, Mailbox.getMailboxFromString(resourceAddress));
            CalendarFolder cf = CalendarFolder.bind(exchangeService, folderId);
            CalendarView calendarView = new CalendarView(dStart, dEnd);

            FindItemsResults<Appointment> findResults = cf.findAppointments(calendarView);

            return findResults
                    .getItems()
                    .stream()
                    .map(this::reloadAppointment)
                    .map(this::mapAppointmentToDTO)
                    .collect(Collectors.toSet());
        } catch(Exception ex) {
            LOGGER.error("Initializing error for service URL \"" + uri + "\". " + ex.getMessage(), ex);
            throw new CommonRbException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        if(exchangeService == null) {
            return;
        }

        exchangeService.close();
    }

    private ExtendedPropertyDefinition getProperty() throws Exception {
        return new ExtendedPropertyDefinition(ORIGINAL_UID_PROPERTY_UID, "OriginalAppointmentUID", MapiPropertyType.String);
    }

    private void initServiceClient() throws Exception {
        try {
            ExchangeVersion exchangeVersion = ExchangeVersion.Exchange2010;

            LOGGER.debug("Initialize service client for \"" + uri + "\". Request server version \"" + exchangeVersion + "\".");

            exchangeService = new ExchangeService(ExchangeVersion.Exchange2010);

            // Set credentials
            String p = password.matches("^\\{.*}$") ? "" : password;

            ExchangeCredentials credentials = new WebCredentials(username, p, ""); // StringUtils.stripToEmpty(c.getDomain()));
            exchangeService.setCredentials(credentials);

            // Set url
            if("true".equalsIgnoreCase(autoDiscover)) {
                exchangeService.autodiscoverUrl(uri);
            } else {
                exchangeService.setUrl(URI.create(uri));
            }
        } catch(Exception ex) {
            LOGGER.error("Initializing error for service URL \"" + uri + "\". " + ex.getMessage(), ex);
            throw ex;
        }
    }

    private Appointment reloadAppointment(@Nonnull Appointment app) throws RuntimeException {
        try {
            String uid = app.getId().getUniqueId();
            app = findAppointmentByUid(uid);
            return app;
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    private Appointment findAppointmentByUid(@Nonnull String uid) throws Exception {
        try {
            return exchangeService.bindToItem(Appointment.class, new ItemId(uid), getPropertySet());
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    private PropertySet getPropertySet() {
        try {
            return new PropertySet(
                    BasePropertySet.FirstClassProperties,
                    AppointmentSchema.Body,
                    getProperty()
            );
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    public AppointmentDTO createAppointment(@Nonnull AppointmentDTO appointment) {
        try {
            Appointment a = new Appointment(exchangeService);
            a.setSubject(appointment.subject());
            a.setBody(MessageBody.getMessageBodyFromText(appointment.body()));
            a.setStart(Date.from(appointment.start().toInstant()));
            a.setEnd(Date.from(appointment.end().toInstant()));
            // a.setExtendedProperty(getProperty(), appointment.getSourceUid());
            a.getResources().add(new Attendee(appointment.resourceMailAddress()));

            a.save();

            return mapAppointmentToDTO(a);
        } catch (Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    public AppointmentDTO update(AppointmentDTO dto) {
        try {
            Appointment app = findAppointmentByUid(dto.uid());
            if (app != null) {
                app.setSubject(dto.subject());
                app.update(ConflictResolutionMode.AutoResolve);
            }
            return mapAppointmentToDTO(app);
        } catch (Exception e) {
            throw new CommonRbException(e);
        }
    }

    public void delete(String appointmentUID) throws Exception {
        Appointment app = findAppointmentByUid(appointmentUID);
        if(app != null) {
            app.delete(DeleteMode.HardDelete, SendCancellationsMode.SendOnlyToAll);
        }
    }

    private AppointmentDTO mapAppointmentToDTO(Appointment appointment) throws RuntimeException {

        try {
            appointment.load(PropertySet.FirstClassProperties);

            return new AppointmentDTO(
                    appointment.getId().getUniqueId(),
                    ZonedDateTime.ofInstant(appointment.getStart().toInstant(), ZoneId.systemDefault()),
                    ZonedDateTime.ofInstant(appointment.getEnd().toInstant(), ZoneId.systemDefault()),
                    appointment.getSubject(),
                    MessageBody.getStringFromMessageBody(appointment.getBody()),
                    appointment.getResources().getItems().stream().map(EmailAddress::getAddress).findFirst().orElseThrow()
            );

            /*
            dto.setBody(MessageBody.getStringFromMessageBody(appointment.getBody()));

            Set<AttendeeDTO> set = appointment.getRequiredAttendees().getItems()
                    .stream()
                    .map(attendee->new AttendeeDTO(attendee.getAddress(), attendee.getName()))
                    .collect(Collectors.toSet());

            dto.getAttendees().addAll(set);

            appointment
                    .getExtendedProperties()
                    .getItems()
                    .stream()
                    .filter(p->ORIGINAL_UID_PROPERTY_UID.equals(p.getPropertyDefinition().getPropertySetId()))
                    .map(p->(String)p.getValue())
                    .filter(StringUtils::isNoneBlank)
                    .forEach(uid->dto.setSourceUid(uid));
            */
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}