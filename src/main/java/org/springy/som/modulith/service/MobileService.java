package org.springy.som.modulith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.mobile.Mobile;
import org.springy.som.modulith.repository.MobileRepository;

import java.util.List;

@Slf4j
@Service
public class MobileService {
    private MobileRepository mobileRepository;

    public MobileService(MobileRepository mobileRepository) {
        this.mobileRepository = mobileRepository;
    }

    public Mobile getMobileById(String id) {
        return mobileRepository.findMobileById(id);
    }

    public Mobile saveMobile(Mobile mobile) {
        return mobileRepository.save(mobile);
    }

    public List<Mobile> getMobilesByClass(String classId) {
        return mobileRepository.findAllByClass(classId);
    }

    public List<Mobile> getMobilesByRace(String race) {
        return mobileRepository.findAllByRace(race);
    }

    public List<Mobile> getMobilesByLevelRange(String min, String max) {
        return mobileRepository.findMobilesByLevelRange(Integer.parseInt(min), Integer.parseInt(max));
    }

    public List<Mobile> getAllMobiles() {
        return mobileRepository.findAll();
    }

    public long deleteAllMobiles() {
        long mobileCount = mobileRepository.count();
        mobileRepository.deleteAll();
        return mobileCount;
    }
}
