package com.setupbuilder.config;

import com.setupbuilder.entity.Component;
import com.setupbuilder.entity.enums.ComponentCategory;
import com.setupbuilder.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final ComponentRepository repo;

    // ═══ Category → image URL ═══
    private static final Map<ComponentCategory, String> CATEGORY_IMAGES = Map.of(
            ComponentCategory.CPU,           "https://images.unsplash.com/photo-1555617981-dac3880eac6e?w=600&q=80",
            ComponentCategory.GPU,           "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600&q=80",
            ComponentCategory.MOTHERBOARD,   "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=600&q=80",
            ComponentCategory.RAM,           "https://images.unsplash.com/photo-1562976540-1502c2145186?w=600&q=80",
            ComponentCategory.STORAGE,       "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=600&q=80",
            ComponentCategory.POWER_SUPPLY,  "https://images.unsplash.com/photo-1591405351990-4726e331f141?w=600&q=80",
            ComponentCategory.CASE,          "https://images.unsplash.com/photo-1541029071515-84cc54f84dc5?w=600&q=80"
    );

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            log.info("Skipping seed — {} components already exist.", repo.count());
            return;
        }

        log.info("Seeding components with images...");

        repo.saveAll(List.of(
                // ═══ CPUs ═══
                cpu("AMD Ryzen 5 7600X", "AMD", "7600X", 950,
                        Map.of("socket", "AM5", "cores", "6", "tdp", "105")),
                cpu("AMD Ryzen 7 7800X3D", "AMD", "7800X3D", 1750,
                        Map.of("socket", "AM5", "cores", "8", "tdp", "120")),
                cpu("AMD Ryzen 9 7950X", "AMD", "7950X", 2100,
                        Map.of("socket", "AM5", "cores", "16", "tdp", "170")),
                cpu("AMD Ryzen 5 5600X", "AMD", "5600X", 550,
                        Map.of("socket", "AM4", "cores", "6", "tdp", "65")),
                cpu("Intel Core i5-14600K", "Intel", "i5-14600K", 1050,
                        Map.of("socket", "LGA1700", "cores", "14", "tdp", "125")),
                cpu("Intel Core i7-14700K", "Intel", "i7-14700K", 1600,
                        Map.of("socket", "LGA1700", "cores", "20", "tdp", "125")),
                cpu("Intel Core i9-14900K", "Intel", "i9-14900K", 2200,
                        Map.of("socket", "LGA1700", "cores", "24", "tdp", "253")),
                cpu("Intel Core i5-13400F", "Intel", "i5-13400F", 750,
                        Map.of("socket", "LGA1700", "cores", "10", "tdp", "65")),

                // ═══ GPUs ═══
                gpu("NVIDIA RTX 4060", "NVIDIA", "RTX 4060", 1300,
                        Map.of("tdp", "115", "vram", "8GB")),
                gpu("NVIDIA RTX 4060 Ti", "NVIDIA", "RTX 4060 Ti", 1700,
                        Map.of("tdp", "160", "vram", "8GB")),
                gpu("NVIDIA RTX 4070 Super", "NVIDIA", "RTX 4070 Super", 2400,
                        Map.of("tdp", "220", "vram", "12GB")),
                gpu("NVIDIA RTX 4070 Ti Super", "NVIDIA", "RTX 4070 Ti Super", 3500,
                        Map.of("tdp", "285", "vram", "16GB")),
                gpu("NVIDIA RTX 4080 Super", "NVIDIA", "RTX 4080 Super", 5000,
                        Map.of("tdp", "320", "vram", "16GB")),
                gpu("AMD Radeon RX 7600", "AMD", "RX 7600", 1100,
                        Map.of("tdp", "165", "vram", "8GB")),
                gpu("AMD Radeon RX 7800 XT", "AMD", "RX 7800 XT", 2800,
                        Map.of("tdp", "263", "vram", "16GB")),
                gpu("AMD Radeon RX 7900 XTX", "AMD", "RX 7900 XTX", 4200,
                        Map.of("tdp", "355", "vram", "24GB")),

                // ═══ Motherboards ═══
                mb("ASUS TUF B650-Plus WiFi", "ASUS", "TUF B650-Plus", 850,
                        Map.of("socket", "AM5", "memory_type", "DDR5", "chipset", "B650")),
                mb("Gigabyte B650 Aorus Elite", "Gigabyte", "B650 Aorus Elite", 1100,
                        Map.of("socket", "AM5", "memory_type", "DDR5", "chipset", "B650")),
                mb("MSI MAG B760 Tomahawk", "MSI", "MAG B760 Tomahawk", 950,
                        Map.of("socket", "LGA1700", "memory_type", "DDR5", "chipset", "B760")),
                mb("ASUS ROG Strix Z790-A", "ASUS", "ROG Strix Z790-A", 1800,
                        Map.of("socket", "LGA1700", "memory_type", "DDR5", "chipset", "Z790")),
                mb("MSI MAG B550 Tomahawk", "MSI", "MAG B550 Tomahawk", 650,
                        Map.of("socket", "AM4", "memory_type", "DDR4", "chipset", "B550")),
                mb("ASRock B760M Pro RS", "ASRock", "B760M Pro RS", 700,
                        Map.of("socket", "LGA1700", "memory_type", "DDR5", "chipset", "B760")),

                // ═══ RAM ═══
                ram("Corsair Vengeance DDR5 32GB", "Corsair", "CMK32GX5M2B6000", 520,
                        Map.of("type", "DDR5", "capacity", "32GB", "speed", "6000MHz")),
                ram("G.Skill Trident Z5 DDR5 32GB", "G.Skill", "F5-6000J3038F16GX2", 580,
                        Map.of("type", "DDR5", "capacity", "32GB", "speed", "6000MHz")),
                ram("Kingston Fury Beast DDR5 16GB", "Kingston", "KF552C40BB-16", 280,
                        Map.of("type", "DDR5", "capacity", "16GB", "speed", "5200MHz")),
                ram("Corsair Vengeance LPX DDR4 16GB", "Corsair", "CMK16GX4M2B3200C16", 220,
                        Map.of("type", "DDR4", "capacity", "16GB", "speed", "3200MHz")),
                ram("G.Skill Ripjaws V DDR4 32GB", "G.Skill", "F4-3600C18D-32GVK", 380,
                        Map.of("type", "DDR4", "capacity", "32GB", "speed", "3600MHz")),

                // ═══ Storage ═══
                storage("Samsung 990 Pro 1TB NVMe", "Samsung", "MZ-V9P1T0BW", 490,
                        Map.of("type", "NVMe", "capacity", "1TB")),
                storage("Crucial P3 Plus 2TB NVMe", "Crucial", "CT2000P3PSSD8", 470,
                        Map.of("type", "NVMe", "capacity", "2TB")),
                storage("Samsung 980 Pro 500GB NVMe", "Samsung", "MZ-V8P500BW", 320,
                        Map.of("type", "NVMe", "capacity", "500GB")),
                storage("WD Black SN850X 1TB NVMe", "WD", "WDS100T2X0E", 520,
                        Map.of("type", "NVMe", "capacity", "1TB")),
                storage("Seagate Barracuda 2TB HDD", "Seagate", "ST2000DM008", 250,
                        Map.of("type", "HDD", "capacity", "2TB")),

                // ═══ Power Supplies ═══
                psu("Corsair RM750e 750W", "Corsair", "RM750e", 420,
                        Map.of("wattage", "750", "efficiency", "80+ Gold")),
                psu("Seasonic Focus GX-850", "Seasonic", "Focus GX-850", 620,
                        Map.of("wattage", "850", "efficiency", "80+ Gold")),
                psu("be quiet! Pure Power 12 M 650W", "be quiet!", "BN342", 350,
                        Map.of("wattage", "650", "efficiency", "80+ Gold")),
                psu("Corsair RM850x 850W", "Corsair", "RM850x", 550,
                        Map.of("wattage", "850", "efficiency", "80+ Gold")),
                psu("EVGA SuperNOVA 650 G5", "EVGA", "220-G5-0650-X1", 380,
                        Map.of("wattage", "650", "efficiency", "80+ Gold")),

                // ═══ Cases ═══
                pcCase("NZXT H5 Flow", "NZXT", "H5 Flow", 420,
                        Map.of("form_factor", "ATX Mid Tower")),
                pcCase("Fractal Design North", "Fractal", "North", 620,
                        Map.of("form_factor", "ATX Mid Tower")),
                pcCase("Lian Li PC-O11 Dynamic", "Lian Li", "PC-O11DW", 550,
                        Map.of("form_factor", "ATX Mid Tower")),
                pcCase("Cooler Master MasterBox TD500", "Cooler Master", "TD500 Mesh V2", 400,
                        Map.of("form_factor", "ATX Mid Tower")),
                pcCase("NZXT H510", "NZXT", "H510", 460,
                        Map.of("form_factor", "ATX Mid Tower")),
                pcCase("Corsair 4000D Airflow", "Corsair", "4000D", 450,
                        Map.of("form_factor", "ATX Mid Tower"))
        ));

        log.info("Seed complete. {} components available.", repo.count());
    }

    // ─── Factory helpers ───
    private Component cpu(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.CPU);
    }
    private Component gpu(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.GPU);
    }
    private Component mb(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.MOTHERBOARD);
    }
    private Component ram(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.RAM);
    }
    private Component storage(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.STORAGE);
    }
    private Component psu(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.POWER_SUPPLY);
    }
    private Component pcCase(String name, String brand, String model, int price, Map<String, String> specs) {
        return make(name, brand, model, price, specs, ComponentCategory.CASE);
    }

    private Component make(String name, String brand, String model, int price,
                           Map<String, String> specs, ComponentCategory cat) {
        return Component.builder()
                .name(name)
                .brand(brand)
                .model(model)
                .category(cat)
                .specs(specs)
                .imageUrl(CATEGORY_IMAGES.get(cat))   // ← Image added here
                .fallbackPrice(BigDecimal.valueOf(price))
                .active(true)
                .build();
    }
}