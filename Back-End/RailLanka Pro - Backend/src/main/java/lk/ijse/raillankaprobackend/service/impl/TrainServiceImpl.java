package lk.ijse.raillankaprobackend.service.impl;

import lk.ijse.raillankaprobackend.dto.TrainDto;
import lk.ijse.raillankaprobackend.entity.Dtypes.CargoType;
import lk.ijse.raillankaprobackend.entity.Dtypes.SpecialTrainType;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainCategory;
import lk.ijse.raillankaprobackend.entity.Dtypes.TrainType;
import lk.ijse.raillankaprobackend.entity.GoodsTrain;
import lk.ijse.raillankaprobackend.entity.SpecialTrain;
import lk.ijse.raillankaprobackend.entity.Station;
import lk.ijse.raillankaprobackend.entity.Train;
import lk.ijse.raillankaprobackend.entity.projection.TrainProjection;
import lk.ijse.raillankaprobackend.exception.IdGenerateLimitReachedException;
import lk.ijse.raillankaprobackend.exception.TrainNameAlreadyExistsException;
import lk.ijse.raillankaprobackend.repository.*;
import lk.ijse.raillankaprobackend.service.TrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;

    private final StationRepository stationRepository;

    private final GoodsTrainRepository goodsTrainRepository;

    private final SpecialTrainRepository specialTrainRepository;


    @Override
    public String generateNewTrainId() {
        if (trainRepository.getLastTrainId().isPresent()) {
            String lastTrainId = trainRepository.getLastTrainId().get();
            String[] split = lastTrainId.split("-");
            int prefixNumber = Integer.parseInt(split[0].substring(3));
            int suffixNumber = Integer.parseInt(split[1]);

            suffixNumber++;

            if (suffixNumber > 99999){
                suffixNumber = 1;
                prefixNumber++;

                if (prefixNumber > 99999){
                    throw new IdGenerateLimitReachedException("All available Train IDs have been used. Please contact the system administrator");
                }
            }
            return String.format("TRN%05d-%05d", prefixNumber, suffixNumber);
        }
        return "TRN00000-00001";
    }

    @Transactional
    @Override
    public String registerTrain(TrainDto trainDto) {

        if (trainRepository.findByName(trainDto.getTrainName()).isPresent()) {
            throw new TrainNameAlreadyExistsException("This train name is already taken. Please choose a different one.");
        }

        List<Station> stations = trainDto.getStations().stream()
                .map(name -> stationRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException(
                                "Station '" + name + "' does not exist in the database"
                        )))
                .toList();



        Train train = Train.builder()
                .trainId(generateNewTrainId())
                .name(formattedTrainName(trainDto.getTrainName()))
                .category(TrainCategory.valueOf(trainDto.getCategory()))
                .trainType(TrainType.valueOf(trainDto.getTrainType()))
                .classes(trainDto.getClasses())
                .active(true)
                .stations(stations)
                .build();

        if (TrainCategory.GOODS.equals(train.getCategory())) {
            GoodsTrain goodsTrain = GoodsTrain.builder()
                    .cargoType(CargoType.valueOf(trainDto.getCargoType()))
                    .capacity(trainDto.getCapacity())
                    .train(train)
                    .build();
            train.setGoodsTrain(goodsTrain);

            goodsTrainRepository.save(goodsTrain);

        } else if (TrainCategory.SPECIAL.equals(train.getCategory())) {
            System.out.println("manuth");
            System.out.println(trainDto.getSpecialTrainType());
            SpecialTrain specialTrain = SpecialTrain.builder()
                    .specialTrainType(SpecialTrainType.valueOf(trainDto.getSpecialTrainType()))
                    .specialFeatures(trainDto.getSpecialFeatures())
                    .train(train)
                    .build();
            train.setSpecialTrain(specialTrain);
            specialTrainRepository.save(specialTrain);

        }

        trainRepository.save(train);

        return "Train has been registered successfully.";
    }

    @Override
    public Page<TrainDto> getAllTrainsAndStopStationCount(int pageNo , int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);


        Page<TrainProjection> projections = trainRepository.findAllWithStopCount(pageable);

        return projections.map(p -> TrainDto.builder()
                .trainId(p.getTrainId())
                .trainName(p.getName())
                .category(p.getCategory())
                .trainType(p.getTrainType())
                .classes(p.getClasses())
                .active(p.getActive())
                .stopStationCount(p.getStopStationCount())
                .stations(Collections.emptyList())
                .build()
        );
    }

    @Override
    public String formattedTrainName(String trainName) {
        if (trainName == null || trainName.isEmpty()) {
            return trainName;
        }

        String[] words = trainName.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formatted.append(word.substring(1).toLowerCase());
                }
                formatted.append(" ");
            }
        }
        return formatted.toString().trim();
    }

    @Override
    public String changeTrainStatus(String trainId, boolean status) {
        Train train = trainRepository.findById(trainId).orElseThrow(
                () -> new RuntimeException("Train ID " + trainId + " does not exist"));
        train.setActive(status);
        trainRepository.save(train);

        return "Train has been successfully set to " + (status ? "Active" : "Inactive");
    }

    @Override
    public String deleteTrain(String trainId) {
        Train train = trainRepository.findById(trainId).orElseThrow(
                () -> new RuntimeException("Train ID " + trainId + " does not exist"));

        trainRepository.delete(train);
        return "Train has been successfully deleted.";
    }

    @Override
    public Page<TrainDto> filterTrainsByKeyword(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<TrainProjection> projections = trainRepository.filterTrainsByKeyword(keyword , pageable);

        return projections.map(p -> TrainDto.builder()
                .trainId(p.getTrainId())
                .trainName(p.getName())
                .category(p.getCategory())
                .trainType(p.getTrainType())
                .classes(p.getClasses())
                .active(p.getActive())
                .stopStationCount(p.getStopStationCount())
                .stations(Collections.emptyList())
                .build()
        );
    }

    @Override
    public Page<TrainDto> filterTrainsByCategory(String category, int pageNo, int pageSize) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page number cannot be less than 1");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<TrainProjection> projections = trainRepository.filterTrainsByCategory(category , pageable);

        return projections.map(p -> TrainDto.builder()
                .trainId(p.getTrainId())
                .trainName(p.getName())
                .category(p.getCategory())
                .trainType(p.getTrainType())
                .classes(p.getClasses())
                .active(p.getActive())
                .stopStationCount(p.getStopStationCount())
                .stations(Collections.emptyList())
                .build()
        );
    }

    @Override
    public List<String> getStopingStationNamesByTrainId(String trainId) {
        return trainRepository.findById(trainId)
                .map(train -> trainRepository.findStationNamesByTrainId(trainId))
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + trainId));
    }

    @Override
    public TrainDto getTrainsAndStationDetailsByTrainId(String trainId) {
        if (trainRepository.findById(trainId).isPresent()) {
            TrainProjection trainStationProjectionById = trainRepository.findTrainProjectionById(trainId);

            String stationNames = trainStationProjectionById.getStationNames();
            String[] split = stationNames.split(",");
            List<String> stationNamesList = List.of(split);

            TrainDto trainDto = new TrainDto();

            trainDto.setTrainId(trainStationProjectionById.getTrainId());
            trainDto.setTrainName(trainStationProjectionById.getName());
            trainDto.setCategory(trainStationProjectionById.getCategory());
            trainDto.setTrainType(trainStationProjectionById.getTrainType());
            trainDto.setClasses(trainStationProjectionById.getClasses());
            trainDto.setStopStationCount(trainStationProjectionById.getStopStationCount());
            trainDto.setCargoType(trainStationProjectionById.getCargoType());
            trainDto.setCapacity(trainStationProjectionById.getCapacity());
            trainDto.setSpecialTrainType(trainStationProjectionById.getSpecialTrainType());
            trainDto.setSpecialFeatures(trainStationProjectionById.getSpecialFeatures());
            trainDto.setActive(trainStationProjectionById.getActive());

            trainDto.setStations(stationNamesList);

            return trainDto;


        }
        throw new RuntimeException("Train not found with id: " + trainId);
    }

    @Override
    @Transactional
    public String updateTrainDetails(TrainDto trainDto) {
        Train train = trainRepository.findById(trainDto.getTrainId())
                .orElseThrow(() -> new RuntimeException(
                        "Train not found with id: " + trainDto.getTrainId()
                ));

        Optional<Train> existingTrainWithName = trainRepository.findByName(trainDto.getTrainName());
        if (existingTrainWithName.isPresent() &&
                !existingTrainWithName.get().getTrainId().equals(trainDto.getTrainId())) {
            throw new TrainNameAlreadyExistsException(
                    "This train name is already taken. Please choose a different one."
            );
        }

        System.out.println(trainDto.getStations());

        List<Station> stations = trainDto.getStations().stream()
                .map(name -> stationRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException(
                                "Station '" + name + "' does not exist in the database"
                        )))
                .collect(Collectors.toList()); // mutable ArrayList


        train.setName(formattedTrainName(trainDto.getTrainName()));
        train.setCategory(TrainCategory.valueOf(trainDto.getCategory()));
        train.setTrainType(TrainType.valueOf(trainDto.getTrainType()));
        train.setClasses(trainDto.getClasses());
        train.setStations(stations);


        if (TrainCategory.GOODS.equals(train.getCategory())) {
            GoodsTrain goodsTrain = train.getGoodsTrain();
            if (goodsTrain == null) {
                goodsTrain = new GoodsTrain();
                goodsTrain.setTrain(train);
            }
            goodsTrain.setCargoType(CargoType.valueOf(trainDto.getCargoType()));
            goodsTrain.setCapacity(trainDto.getCapacity());
            train.setGoodsTrain(goodsTrain);
            goodsTrainRepository.save(goodsTrain);
        } else {
            if (train.getGoodsTrain() != null) {
                goodsTrainRepository.delete(train.getGoodsTrain());
                train.setGoodsTrain(null);
            }
        }

        if (TrainCategory.SPECIAL.equals(train.getCategory())) {
            SpecialTrain specialTrain = train.getSpecialTrain();
            if (specialTrain == null) {
                specialTrain = new SpecialTrain();
                specialTrain.setTrain(train);
            }
            specialTrain.setSpecialTrainType(SpecialTrainType.valueOf(trainDto.getSpecialTrainType()));
            specialTrain.setSpecialFeatures(trainDto.getSpecialFeatures());
            train.setSpecialTrain(specialTrain);
            specialTrainRepository.save(specialTrain);
        } else {
            if (train.getSpecialTrain() != null) {
                specialTrainRepository.delete(train.getSpecialTrain());
                train.setSpecialTrain(null);
            }
        }

        trainRepository.save(train);

        return "Train details have been updated successfully.";
    }

}
