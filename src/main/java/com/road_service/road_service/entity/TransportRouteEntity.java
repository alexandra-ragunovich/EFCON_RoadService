package com.road_service.road_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name=TransportRouteEntity.TABLE_NAME)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransportRouteEntity {

    public static final String TABLE_NAME = "transport_routes";
    public static final String ID = "id";
    public static final String FROM_CITY_ID = "from_city_id";
    public static final String TO_CITY_ID = "to_city_id";
    public static final String TRANSPORT_TYPE = "transport_type";
    public static final String DURATION_HOURS = "duration_hours";
    public static final String CARRIER = "carrier";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @ManyToOne
    @JoinColumn(name = FROM_CITY_ID, nullable = false)
    private CityEntity fromCity;

    @ManyToOne
    @JoinColumn(name = TO_CITY_ID, nullable = false)
    private CityEntity toCity;

    @Column(name = TRANSPORT_TYPE)
    private String transportType;

    @Column(name = DURATION_HOURS)
    private double durationHours;

    @Column(name = CARRIER)
    private String carrier;
}