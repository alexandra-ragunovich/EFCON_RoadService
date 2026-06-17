package com.road_service.road_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name=CityEntity.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = {"name", "country"}))
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CityEntity {

    public static final String TABLE_NAME = "cities";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=ID)
    private long id;

    @Column(name=NAME)
    private String name;

    @Column(name=COUNTRY)
    private String country;

    @Column(name=LATITUDE)
    private double latitude;

    @Column(name=LONGITUDE)
    private double longitude;

    @Column(length = 3)
    private String iataCode;

    @Column(length = 20)
    private String yandexCode;
}
