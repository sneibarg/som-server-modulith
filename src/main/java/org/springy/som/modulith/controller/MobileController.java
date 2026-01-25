package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.MobileService;
import org.springy.som.modulith.domain.mobile.Mobile;

import java.util.List;

@RestController
public class MobileController {
    private final MobileService mobileService;

    public  MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @PostMapping("/api/v1/mobile")
    @ResponseBody
    public Mobile createMobile(@RequestBody Mobile mobile) {
        return mobileService.saveMobile(mobile);
    }

    @GetMapping(path = "/api/v1/mobiles")
    @ResponseBody
    public List<Mobile> getMobiles() {
        return mobileService.getAllMobiles();
    }

    @GetMapping(path = "/api/v1/mobiles/race")
    @ResponseBody
    public List<Mobile> getMobilesByRace(@RequestParam String raceId) {
        return mobileService.getMobilesByRace(raceId);
    }

    @GetMapping(path = "/api/v1/mobiles/class")
    @ResponseBody
    public List<Mobile> getMobilesByClass(@RequestParam String classId) {
        return mobileService.getMobilesByClass(classId);
    }

    @GetMapping(path = "/api/v1/mobiles/range")
    @ResponseBody
    public List<Mobile> getMobilesByLevelRange(@RequestParam String min, @RequestParam String max) {
        return mobileService.getMobilesByLevelRange(min, max);
    }

    @GetMapping(path = "/api/v1/mobile")
    @ResponseBody
    public Mobile getMobileById(@RequestParam String mobileId) {
        return mobileService.getMobileById(mobileId);
    }

    @PutMapping(path = "/api/v1/mobile")
    @ResponseBody
    public Mobile updateMobile(@RequestParam Mobile mobile) {
        return mobileService.saveMobile(mobile);
    }

    @DeleteMapping(path = "/api/v1/mobiles")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(mobileService.deleteAllMobiles())
                .append(" Mobile objects.");

        return response.toString();
    }
}
