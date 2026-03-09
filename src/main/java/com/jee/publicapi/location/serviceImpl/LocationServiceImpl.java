package com.jee.publicapi.location.serviceImpl;

import org.springframework.stereotype.Service;

import com.jee.publicapi.location.dto.LocationDTO;
import com.jee.publicapi.location.entity.City;
import com.jee.publicapi.location.entity.Country;
import com.jee.publicapi.location.entity.District;
import com.jee.publicapi.location.entity.Pincode;
import com.jee.publicapi.location.entity.State;
import com.jee.publicapi.location.repository.CityRepository;
import com.jee.publicapi.location.repository.CountryRepository;
import com.jee.publicapi.location.repository.DistrictRepository;
import com.jee.publicapi.location.repository.PincodeRepository;
import com.jee.publicapi.location.repository.StateRepository;
import com.jee.publicapi.location.service.LocationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final CountryRepository countryRepo;
    private final StateRepository stateRepo;
    private final DistrictRepository districtRepo;
    private final CityRepository cityRepo;
    private final PincodeRepository pincodeRepo;

    public LocationServiceImpl(CountryRepository countryRepo,
                               StateRepository stateRepo,
                               DistrictRepository districtRepo,
                               CityRepository cityRepo,
                               PincodeRepository pincodeRepo) {
        this.countryRepo = countryRepo;
        this.stateRepo = stateRepo;
        this.districtRepo = districtRepo;
        this.cityRepo = cityRepo;
        this.pincodeRepo = pincodeRepo;
    }

    @Override
    public List<LocationDTO> getCountries() {
        System.out.println("Fetching all countries...");
        List<Country> countries = countryRepo.findAll();
        System.out.println("Countries fetched: " + countries.size());
        return countries.stream()
                .map(c -> new LocationDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getStates(Long countryId) {
        return stateRepo.findByCountryId(countryId).stream()
                .map(s -> new LocationDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getDistricts(Long stateId) {
        return districtRepo.findByStateId(stateId).stream()
                .map(d -> new LocationDTO(d.getId(), d.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getCities(Long districtId) {
        return cityRepo.findByDistrictId(districtId).stream()
                .map(c -> new LocationDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getPincodes(Long cityId) {
        return pincodeRepo.findByCityId(cityId).stream()
                .map(p -> new LocationDTO(p.getId(), p.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO getByPincode(String pincode) {
        Optional<Pincode> pinOpt = pincodeRepo.findByCode(pincode);
        if (pinOpt.isEmpty()) return null;

        Pincode pin = pinOpt.get();
        City city = pin.getCity();
        District district = city != null ? city.getDistrict() : null;
        State state = district != null ? district.getState() : null;
        Country country = state != null ? state.getCountry() : null;

        LocationDTO dto = new LocationDTO();
        dto.setPincodeId(pin != null ? pin.getId() : null);
        dto.setCityId(city != null ? city.getId() : null);
        dto.setDistrictId(district != null ? district.getId() : null);
        dto.setStateId(state != null ? state.getId() : null);
        dto.setCountryId(country != null ? country.getId() : null);

        return dto;
    }

}