package com.nob.demo.springelk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/")
    public String getAll() {
        log.info("[ALL ITEM] fetching all items from database");
        log.info("[ALL ITEM] fetch all items completed");
        return "All item";
    }

    @GetMapping("/{id}")
    public String getOne(@PathVariable Long id) {
        log.info("[ITEM] fetching item, id");
        log.info("[ITEM] successfully fetch item");
        return "One item";
    }

    @GetMapping("/detail")
    public String getDetail() {
        log.info("[DETAIL] fetching detail");
        log.info("[DETAIL] successfully fetch detail");
        return "One item";
    }

    @PostMapping
    public String create() {
        log.info("[CREATE ITEM] trying to create item");
        log.info("[CREATE ITEM] successfully created item");
        return "Item is created";
    }

    @PutMapping("/id")
    public String update() {
        log.info("[UPDATE ITEM] updating item with id");
        log.info("[UPDATE ITEM] successfully updated item");
        return "Item is updated";
    }

    @DeleteMapping("/id")
    public String delete() {
        log.info("[DELETE ITEM] deleting item with id");
        log.info("[UPDATE ITEM] successfully deleted item");
        return "Item is deleted";
    }

}
