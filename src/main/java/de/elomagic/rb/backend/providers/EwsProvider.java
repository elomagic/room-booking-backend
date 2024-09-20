package de.elomagic.rb.backend.providers;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

import de.elomagic.rb.backend.exceptions.CommonRbException;
import de.elomagic.rb.backend.dtos.AppointmentDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("!test")
@ConditionalOnExpression("'${rb.ext.apiType}' == 'ews'")
public class EwsProvider implements IProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EwsProvider.class);

    private static final UUID ORIGINAL_UID_PROPERTY_UID = UUID.fromString("84178b2e-50a0-46fa-85b9-d87fc5185886");

    private ExchangeService exchangeService;

    @Value("${rb.ext.ews.uri}")
    private String uri;
    @Value("${rb.ext.ews.autoDiscover:false}")
    private String autoDiscover;
    @Value("${rb.ext.ews.credentials.username}")
    private String username;
    @Value("${rb.ext.ews.credentials.password}")
    private String password;

    @PostConstruct
    public void startSession() throws Exception {
        initServiceClient();
    }

    @Nonnull
    @Override
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
                    .map(a -> mapAppointmentToDTO(a, resourceAddress))
                    .collect(Collectors.toSet());
        } catch(Exception ex) {
            LOGGER.error("Initializing error for service URL \"" + uri + "\". " + ex.getMessage(), ex);
            throw new CommonRbException(ex);
        }
    }

    @Override
    public void close() {
        if(exchangeService == null) {
            return;
        }

        exchangeService.close();
    }

    @Nonnull
    private ExtendedPropertyDefinition getProperty() throws Exception {
        return new ExtendedPropertyDefinition(ORIGINAL_UID_PROPERTY_UID, "OriginalAppointmentUID", MapiPropertyType.String);
    }

    private void initServiceClient() throws Exception {
        try {
            ExchangeVersion exchangeVersion = ExchangeVersion.Exchange2010;

            LOGGER.debug("Initialize service client for \"{}}\". Request server version \"{}\".", uri, exchangeVersion);

            exchangeService = new ExchangeService(ExchangeVersion.Exchange2010);

            // Set credentials
            String p = password.matches("^\\{.*}$") ? "" : password;

            ExchangeCredentials credentials = new WebCredentials(username, p, "");
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

    @Nonnull
    private Appointment reloadAppointment(@Nonnull Appointment app) throws CommonRbException {
        try {
            String uid = app.getId().getUniqueId();
            return findAppointmentByUid(uid);
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

    @Nonnull
    private PropertySet getPropertySet() {
        try {
            return new PropertySet(
                    BasePropertySet.FirstClassProperties,
                    ItemSchema.Body,
                    getProperty()
            );
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    @Nonnull
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

            return mapAppointmentToDTO(a, appointment.resourceMailAddress());
        } catch (Exception ex) {
            throw new CommonRbException(ex);
        }
    }

    @Nullable
    public AppointmentDTO update(@Nonnull AppointmentDTO dto) {
        if (dto.uid() == null) {
            return null;
        }

        try {
            Appointment app = findAppointmentByUid(dto.uid());
            if (app != null) {
                app.setSubject(dto.subject());
                app.update(ConflictResolutionMode.AutoResolve);
            }
            return app == null ? null : mapAppointmentToDTO(app, dto.resourceMailAddress());
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

    @Nonnull
    private AppointmentDTO mapAppointmentToDTO(@Nonnull Appointment appointment, @Nonnull String resourceMailAddress) throws CommonRbException {
        try {
            appointment.load(PropertySet.FirstClassProperties);

            return new AppointmentDTO(
                    appointment.getId().getUniqueId(),
                    ZonedDateTime.ofInstant(appointment.getStart().toInstant(), ZoneId.systemDefault()),
                    ZonedDateTime.ofInstant(appointment.getEnd().toInstant(), ZoneId.systemDefault()),
                    appointment.getSubject(),
                    MessageBody.getStringFromMessageBody(appointment.getBody()),
                    resourceMailAddress
            );
        } catch(Exception ex) {
            throw new CommonRbException(ex);
        }
    }

}