package com.setupbuilder.service;

import com.setupbuilder.entity.BuildComponent;
import com.setupbuilder.entity.enums.ComponentCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompatibilityService {

    /** Returns a list of human-readable warnings (empty = all good). */
    public List<String> check(List<BuildComponent> components) {
        List<String> warnings = new ArrayList<>();

        Optional<BuildComponent> cpu = findByCategory(components, ComponentCategory.CPU);
        Optional<BuildComponent> motherboard = findByCategory(components, ComponentCategory.MOTHERBOARD);
        Optional<BuildComponent> gpu = findByCategory(components, ComponentCategory.GPU);
        Optional<BuildComponent> psu = findByCategory(components, ComponentCategory.POWER_SUPPLY);
        Optional<BuildComponent> ram = findByCategory(components, ComponentCategory.RAM);

        // 1. CPU socket vs Motherboard socket
        if (cpu.isPresent() && motherboard.isPresent()) {
            String cpuSocket = spec(cpu.get(), "socket");
            String mbSocket = spec(motherboard.get(), "socket");
            if (cpuSocket != null && mbSocket != null && !cpuSocket.equalsIgnoreCase(mbSocket)) {
                warnings.add("CPU socket (" + cpuSocket + ") does not match motherboard socket (" + mbSocket + ").");
            }
        }

        // 2. RAM type vs Motherboard memory type
        if (ram.isPresent() && motherboard.isPresent()) {
            String ramType = spec(ram.get(), "type");           // e.g. DDR5
            String mbMemType = spec(motherboard.get(), "memory_type");
            if (ramType != null && mbMemType != null && !ramType.equalsIgnoreCase(mbMemType)) {
                warnings.add("RAM type (" + ramType + ") does not match motherboard memory type (" + mbMemType + ").");
            }
        }

        // 3. PSU wattage vs estimated draw
        if (psu.isPresent()) {
            Integer psuWatt = parseInt(spec(psu.get(), "wattage"));
            int estDraw = 150; // baseline for board, RAM, storage, fans
            if (cpu.isPresent()) estDraw += parseIntOrDefault(spec(cpu.get(), "tdp"), 100);
            if (gpu.isPresent()) estDraw += parseIntOrDefault(spec(gpu.get(), "tdp"), 200);
            int recommended = estDraw + 100; // headroom

            if (psuWatt != null && psuWatt < recommended) {
                warnings.add("Power supply (" + psuWatt + "W) may be insufficient. Estimated draw: "
                        + estDraw + "W, recommended: " + recommended + "W.");
            }
        }

        // 4. Missing essential components
        checkMissing(components, ComponentCategory.CPU, "CPU", warnings);
        checkMissing(components, ComponentCategory.MOTHERBOARD, "Motherboard", warnings);
        checkMissing(components, ComponentCategory.RAM, "RAM", warnings);
        checkMissing(components, ComponentCategory.STORAGE, "Storage", warnings);
        checkMissing(components, ComponentCategory.POWER_SUPPLY, "Power Supply", warnings);
        checkMissing(components, ComponentCategory.CASE, "Case", warnings);

        return warnings;
    }

    private void checkMissing(List<BuildComponent> components, ComponentCategory cat, String label, List<String> warnings) {
        boolean present = components.stream()
                .anyMatch(bc -> bc.getComponent().getCategory() == cat);
        if (!present) warnings.add("No " + label + " selected.");
    }

    private Optional<BuildComponent> findByCategory(List<BuildComponent> components, ComponentCategory cat) {
        return components.stream()
                .filter(bc -> bc.getComponent().getCategory() == cat)
                .findFirst();
    }

    private String spec(BuildComponent bc, String key) {
        Map<String, String> specs = bc.getComponent().getSpecs();
        return specs == null ? null : specs.get(key);
    }

    private Integer parseInt(String s) {
        if (s == null) return null;
        try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return null; }
    }

    private int parseIntOrDefault(String s, int def) {
        Integer v = parseInt(s);
        return v == null ? def : v;
    }
}