package lk.ijse.raillankaprobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.RouteMatcher;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Station {
    @Id
    private String stationId;
    private String name;
    private String stationCode;
    private String district;
    private String province;
    private int noOfPlatforms;

    @Column(name = "platform_length (meter)")
    private long platformLength;
    private String otherFacilities;
    private boolean inService;

    @OneToOne(mappedBy = "station")
    private StationMaster stationMaster;

    @OneToMany(mappedBy = "station")
    private List<Counter> counters;

    @OneToMany(mappedBy = "station")
    private List<Employee> employees;

    @ManyToMany(mappedBy = "stations" )
    private List<Train> trains;

    @OneToMany(mappedBy = "mainDepartureStation")
    private List<Schedule> departureSchedules;

    @OneToMany(mappedBy = "mainArrivalStation")
    private List<Schedule> arrivalSchedules;






}
