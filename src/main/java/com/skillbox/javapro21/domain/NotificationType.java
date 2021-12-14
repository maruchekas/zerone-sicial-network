package com.skillbox.javapro21.domain;

import com.skillbox.javapro21.domain.enumeration.NotificationTypeStatus;

import javax.persistence.*;


@Entity
@Table(name = "notification_type")
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status")
    private NotificationTypeStatus notificationStatus;


    public Integer getId() {
        return this.id;
    }

    public NotificationType id(Integer id) {
        this.setId(id);
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NotificationTypeStatus getNotificationStatus() {
        return this.notificationStatus;
    }

    public NotificationType notificationStatus(NotificationTypeStatus notificationStatus) {
        this.setNotificationStatus(notificationStatus);
        return this;
    }

    public void setNotificationStatus(NotificationTypeStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationType)) {
            return false;
        }
        return id != null && id.equals(((NotificationType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NotificationType{" +
                "id=" + getId() +
                ", notificationStatus='" + getNotificationStatus() + "'" +
                "}";
    }
}
