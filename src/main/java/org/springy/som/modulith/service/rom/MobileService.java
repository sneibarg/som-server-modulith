package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.mobile.Mobile;
import org.springy.som.modulith.repository.rom.MobileRepository;

import java.util.List;

@Slf4j
@RestController
public class MobileService {
    private MobileRepository mobileRepository;

    public MobileService(MobileRepository mobileRepository) {
        this.mobileRepository = mobileRepository;
    }

    @PostMapping("/api/v1/mobile")
    @ResponseBody
    public Mobile createMobile(@RequestBody Mobile mobile) {
        return mobileRepository.save(mobile);
    }

    @GetMapping(path = "/api/v1/mobiles")
    @ResponseBody
    public List<Mobile> getMobiles() {
        return mobileRepository.findAll();
    }

    @GetMapping(path = "/api/v1/mobiles/race")
    @ResponseBody
    public List<Mobile> getMobilesByRace(@RequestParam String raceId) {
        return mobileRepository.findAllByRace(raceId);
    }

    @GetMapping(path = "/api/v1/mobiles/class")
    @ResponseBody
    public List<Mobile> getMobilesByClass(@RequestParam String classId) {
        return mobileRepository.findAllByClass(classId);
    }

    @GetMapping(path = "/api/v1/mobiles/range")
    @ResponseBody
    public List<Mobile> getMobilesByLevelRange(@RequestParam String min, @RequestParam String max) {
        return mobileRepository.findMobilesByLevelRange(Integer.parseInt(min), Integer.parseInt(max));
    }

    @GetMapping(path = "/api/v1/mobile")
    @ResponseBody
    public Mobile getMobileById(@RequestParam String mobileId) {
        return mobileRepository.findMobileById(mobileId);
    }

    @PutMapping(path = "/api/v1/mobile")
    @ResponseBody
    public Mobile updateMobile(@RequestParam String mobileId) {
        return mobileRepository.findMobileById(mobileId);
    }

    @DeleteMapping(path = "/api/v1/mobiles")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = mobileRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" Mobile objects.");
        mobileRepository.deleteAll();
        return response.toString();
    }
}
