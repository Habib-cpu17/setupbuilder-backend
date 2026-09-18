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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final ComponentRepository repo;

    private static final Map<ComponentCategory, String> CATEGORY_IMAGES = Map.of(
            ComponentCategory.CPU,          "https://images.unsplash.com/photo-1555617981-dac3880eac6e?w=700&q=80",
            ComponentCategory.GPU,          "https://images.unsplash.com/photo-1591405351990-4726e331f141?w=700&q=80",
            ComponentCategory.MOTHERBOARD,  "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=700&q=80",
            ComponentCategory.RAM,          "https://images.unsplash.com/photo-1562976540-1502c2145186?w=700&q=80",
            ComponentCategory.STORAGE,      "https://images.unsplash.com/photo-1541029071515-84cc54f84dc5?w=700&q=80",
            ComponentCategory.POWER_SUPPLY, "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=700&q=80",
            ComponentCategory.CASE,         "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=700&q=80"
    );

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            log.info("Skipping seed — {} components already exist.", repo.count());
            return;
        }

        log.info("Seeding components (200+)...");
        List<Component> all = new ArrayList<>();

        // ═══════════════════════════════════════════════════════
        // CPUs — 40
        // ═══════════════════════════════════════════════════════
        // AMD AM4
        all.add(cpu("AMD Ryzen 3 4100",           "AMD", "4100",       320, "AM4",     "4",  "65"));
        all.add(cpu("AMD Ryzen 5 5500",           "AMD", "5500",       420, "AM4",     "6",  "65"));
        all.add(cpu("AMD Ryzen 5 5600",           "AMD", "5600",       550, "AM4",     "6",  "65"));
        all.add(cpu("AMD Ryzen 5 5600X",          "AMD", "5600X",      620, "AM4",     "6",  "65"));
        all.add(cpu("AMD Ryzen 7 5700X",          "AMD", "5700X",      850, "AM4",     "8",  "65"));
        all.add(cpu("AMD Ryzen 7 5700X3D",        "AMD", "5700X3D",    1000, "AM4",    "8",  "105"));
        all.add(cpu("AMD Ryzen 7 5800X",          "AMD", "5800X",      1000, "AM4",    "8",  "105"));
        all.add(cpu("AMD Ryzen 7 5800X3D",        "AMD", "5800X3D",    1500, "AM4",    "8",  "105"));
        all.add(cpu("AMD Ryzen 9 5900X",          "AMD", "5900X",      1400, "AM4",    "12", "105"));
        all.add(cpu("AMD Ryzen 9 5950X",          "AMD", "5950X",      1900, "AM4",    "16", "105"));
        // AMD AM5
        all.add(cpu("AMD Ryzen 5 7600",           "AMD", "7600",       900,  "AM5",    "6",  "65"));
        all.add(cpu("AMD Ryzen 5 7600X",          "AMD", "7600X",      1000, "AM5",    "6",  "105"));
        all.add(cpu("AMD Ryzen 7 7700",           "AMD", "7700",       1200, "AM5",    "8",  "65"));
        all.add(cpu("AMD Ryzen 7 7700X",          "AMD", "7700X",      1350, "AM5",    "8",  "105"));
        all.add(cpu("AMD Ryzen 7 7800X3D",        "AMD", "7800X3D",    1900, "AM5",    "8",  "120"));
        all.add(cpu("AMD Ryzen 9 7900X",          "AMD", "7900X",      1900, "AM5",    "12", "170"));
        all.add(cpu("AMD Ryzen 9 7950X",          "AMD", "7950X",      2200, "AM5",    "16", "170"));
        all.add(cpu("AMD Ryzen 9 7950X3D",        "AMD", "7950X3D",    2900, "AM5",    "16", "120"));
        all.add(cpu("AMD Ryzen 5 9600X",          "AMD", "9600X",      1050, "AM5",    "6",  "65"));
        all.add(cpu("AMD Ryzen 7 9700X",          "AMD", "9700X",      1500, "AM5",    "8",  "65"));
        all.add(cpu("AMD Ryzen 7 9800X3D",        "AMD", "9800X3D",    2400, "AM5",    "8",  "120"));
        all.add(cpu("AMD Ryzen 9 9900X",          "AMD", "9900X",      2100, "AM5",    "12", "120"));
        all.add(cpu("AMD Ryzen 9 9950X",          "AMD", "9950X",      2800, "AM5",    "16", "170"));
        all.add(cpu("AMD Ryzen 9 9950X3D",        "AMD", "9950X3D",    3600, "AM5",    "16", "170"));
        // Intel LGA1700
        all.add(cpu("Intel Core i3-12100F",       "Intel", "i3-12100F", 350, "LGA1700", "4", "58"));
        all.add(cpu("Intel Core i5-12400F",       "Intel", "i5-12400F", 550, "LGA1700", "6", "65"));
        all.add(cpu("Intel Core i5-13400F",       "Intel", "i5-13400F", 750, "LGA1700", "10", "65"));
        all.add(cpu("Intel Core i5-14400F",       "Intel", "i5-14400F", 850, "LGA1700", "10", "65"));
        all.add(cpu("Intel Core i5-14600K",       "Intel", "i5-14600K", 1200, "LGA1700", "14", "125"));
        all.add(cpu("Intel Core i7-12700K",       "Intel", "i7-12700K", 950, "LGA1700", "12", "125"));
        all.add(cpu("Intel Core i7-13700K",       "Intel", "i7-13700K", 1400, "LGA1700", "16", "125"));
        all.add(cpu("Intel Core i7-14700K",       "Intel", "i7-14700K", 1700, "LGA1700", "20", "125"));
        all.add(cpu("Intel Core i9-12900K",       "Intel", "i9-12900K", 1300, "LGA1700", "16", "125"));
        all.add(cpu("Intel Core i9-13900K",       "Intel", "i9-13900K", 1900, "LGA1700", "24", "125"));
        all.add(cpu("Intel Core i9-14900K",       "Intel", "i9-14900K", 2200, "LGA1700", "24", "253"));
        // Intel LGA1851 (Core Ultra)
        all.add(cpu("Intel Core Ultra 5 245K",    "Intel", "Ultra 5 245K", 1400, "LGA1851", "14", "125"));
        all.add(cpu("Intel Core Ultra 7 265K",    "Intel", "Ultra 7 265K", 1700, "LGA1851", "20", "125"));
        all.add(cpu("Intel Core Ultra 9 285K",    "Intel", "Ultra 9 285K", 2700, "LGA1851", "24", "125"));
        all.add(cpu("AMD Ryzen 5 8600G",          "AMD", "8600G",      950,  "AM5",    "6",  "65"));
        all.add(cpu("AMD Ryzen 7 8700G",          "AMD", "8700G",      1300, "AM5",    "8",  "65"));

        // ═══════════════════════════════════════════════════════
        // GPUs — 35
        // ═══════════════════════════════════════════════════════
        all.add(gpu("NVIDIA GTX 1650",            "NVIDIA", "GTX 1650",       600, "75",  "4GB"));
        all.add(gpu("NVIDIA GTX 1660 Super",      "NVIDIA", "GTX 1660 Super", 850, "125", "6GB"));
        all.add(gpu("NVIDIA RTX 3050 8GB",        "NVIDIA", "RTX 3050",       950, "130", "8GB"));
        all.add(gpu("NVIDIA RTX 3060 12GB",       "NVIDIA", "RTX 3060",       1200, "170", "12GB"));
        all.add(gpu("NVIDIA RTX 3060 Ti",         "NVIDIA", "RTX 3060 Ti",    1400, "200", "8GB"));
        all.add(gpu("NVIDIA RTX 3070",            "NVIDIA", "RTX 3070",       1700, "220", "8GB"));
        all.add(gpu("NVIDIA RTX 3070 Ti",         "NVIDIA", "RTX 3070 Ti",    2000, "290", "8GB"));
        all.add(gpu("NVIDIA RTX 3080",            "NVIDIA", "RTX 3080",       2500, "320", "10GB"));
        all.add(gpu("NVIDIA RTX 3080 Ti",         "NVIDIA", "RTX 3080 Ti",    3200, "350", "12GB"));
        all.add(gpu("NVIDIA RTX 3090",            "NVIDIA", "RTX 3090",       4000, "350", "24GB"));
        all.add(gpu("NVIDIA RTX 4060",            "NVIDIA", "RTX 4060",       1300, "115", "8GB"));
        all.add(gpu("NVIDIA RTX 4060 Ti 8GB",     "NVIDIA", "RTX 4060 Ti 8GB", 1600, "160", "8GB"));
        all.add(gpu("NVIDIA RTX 4060 Ti 16GB",    "NVIDIA", "RTX 4060 Ti 16GB", 2000, "165", "16GB"));
        all.add(gpu("NVIDIA RTX 4070",            "NVIDIA", "RTX 4070",       2200, "200", "12GB"));
        all.add(gpu("NVIDIA RTX 4070 Super",      "NVIDIA", "RTX 4070 Super", 2500, "220", "12GB"));
        all.add(gpu("NVIDIA RTX 4070 Ti Super",   "NVIDIA", "RTX 4070 Ti Super", 3200, "285", "16GB"));
        all.add(gpu("NVIDIA RTX 4080 Super",      "NVIDIA", "RTX 4080 Super", 4800, "320", "16GB"));
        all.add(gpu("NVIDIA RTX 4090",            "NVIDIA", "RTX 4090",       7500, "450", "24GB"));
        all.add(gpu("NVIDIA RTX 5060",            "NVIDIA", "RTX 5060",       1500, "145", "8GB"));
        all.add(gpu("NVIDIA RTX 5060 Ti",         "NVIDIA", "RTX 5060 Ti",    2000, "180", "16GB"));
        all.add(gpu("NVIDIA RTX 5070",            "NVIDIA", "RTX 5070",       2500, "250", "12GB"));
        all.add(gpu("NVIDIA RTX 5070 Ti",         "NVIDIA", "RTX 5070 Ti",    3200, "300", "16GB"));
        all.add(gpu("NVIDIA RTX 5080",            "NVIDIA", "RTX 5080",       4800, "360", "16GB"));
        all.add(gpu("NVIDIA RTX 5090",            "NVIDIA", "RTX 5090",       9500, "575", "32GB"));
        // AMD Radeon
        all.add(gpu("AMD Radeon RX 6600",         "AMD", "RX 6600",     950,  "132", "8GB"));
        all.add(gpu("AMD Radeon RX 6700 XT",      "AMD", "RX 6700 XT",  1400, "230", "12GB"));
        all.add(gpu("AMD Radeon RX 6800 XT",      "AMD", "RX 6800 XT",  2300, "300", "16GB"));
        all.add(gpu("AMD Radeon RX 6950 XT",      "AMD", "RX 6950 XT",  3200, "335", "16GB"));
        all.add(gpu("AMD Radeon RX 7600",         "AMD", "RX 7600",     1100, "165", "8GB"));
        all.add(gpu("AMD Radeon RX 7600 XT",      "AMD", "RX 7600 XT",  1400, "190", "16GB"));
        all.add(gpu("AMD Radeon RX 7700 XT",      "AMD", "RX 7700 XT",  1900, "245", "12GB"));
        all.add(gpu("AMD Radeon RX 7800 XT",      "AMD", "RX 7800 XT",  2400, "263", "16GB"));
        all.add(gpu("AMD Radeon RX 7900 XT",      "AMD", "RX 7900 XT",  3000, "315", "20GB"));
        all.add(gpu("AMD Radeon RX 7900 XTX",     "AMD", "RX 7900 XTX", 3800, "355", "24GB"));
        // Intel Arc
        all.add(gpu("Intel Arc A750",             "Intel", "Arc A750", 900,  "225", "8GB"));

        // ═══════════════════════════════════════════════════════
        // Motherboards — 25
        // ═══════════════════════════════════════════════════════
        // AM4
        all.add(mb("MSI A520M-A Pro",             "MSI", "A520M-A Pro",        250, "AM4",    "DDR4", "A520"));
        all.add(mb("Gigabyte B550M DS3H",         "Gigabyte", "B550M DS3H",   400, "AM4",    "DDR4", "B550"));
        all.add(mb("MSI MAG B550 Tomahawk",       "MSI", "MAG B550 Tomahawk",  700, "AM4",    "DDR4", "B550"));
        all.add(mb("ASUS TUF Gaming B550-Plus",   "ASUS", "TUF B550-Plus",     600, "AM4",    "DDR4", "B550"));
        all.add(mb("ASUS ROG Strix B550-F",       "ASUS", "ROG Strix B550-F",  800, "AM4",    "DDR4", "B550"));
        all.add(mb("Gigabyte X570 Aorus Elite",   "Gigabyte", "X570 Aorus Elite", 900, "AM4", "DDR4", "X570"));
        // AM5
        all.add(mb("ASRock B650M-HDV/M.2",        "ASRock", "B650M-HDV/M.2",    500, "AM5", "DDR5", "B650"));
        all.add(mb("Gigabyte B650M DS3H",         "Gigabyte", "B650M DS3H",     550, "AM5", "DDR5", "B650"));
        all.add(mb("MSI PRO B650M-A WiFi",        "MSI", "PRO B650M-A WiFi",   650, "AM5", "DDR5", "B650"));
        all.add(mb("ASUS TUF Gaming B650-Plus",   "ASUS", "TUF B650-Plus WiFi", 800, "AM5", "DDR5", "B650"));
        all.add(mb("Gigabyte B650 Aorus Elite AX", "Gigabyte", "B650 Aorus Elite", 1000, "AM5", "DDR5", "B650"));
        all.add(mb("MSI MAG B650 Tomahawk WiFi",  "MSI", "MAG B650 Tomahawk",  1050, "AM5", "DDR5", "B650"));
        all.add(mb("ASUS ROG Strix B650E-F",      "ASUS", "ROG B650E-F",       1300, "AM5", "DDR5", "B650E"));
        all.add(mb("Gigabyte X670 Aorus Elite AX", "Gigabyte", "X670 Aorus Elite", 1500, "AM5", "DDR5", "X670"));
        all.add(mb("ASUS ROG Strix X670E-E",      "ASUS", "ROG X670E-E",       2000, "AM5", "DDR5", "X670E"));
        // Intel LGA1700
        all.add(mb("ASRock H610M-HDV",            "ASRock", "H610M-HDV",       300, "LGA1700", "DDR4", "H610"));
        all.add(mb("Gigabyte B660M DS3H",         "Gigabyte", "B660M DS3H",    450, "LGA1700", "DDR4", "B660"));
        all.add(mb("MSI PRO B660M-A WiFi",        "MSI", "PRO B660M-A WiFi",   550, "LGA1700", "DDR4", "B660"));
        all.add(mb("ASUS TUF Gaming B760-Plus",   "ASUS", "TUF B760-Plus WiFi", 850, "LGA1700", "DDR5", "B760"));
        all.add(mb("MSI MAG B760 Tomahawk",       "MSI", "MAG B760 Tomahawk",  900, "LGA1700", "DDR5", "B760"));
        all.add(mb("Gigabyte B760 Aorus Elite",   "Gigabyte", "B760 Aorus Elite", 1050, "LGA1700", "DDR5", "B760"));
        all.add(mb("ASUS ROG Strix Z790-A",       "ASUS", "ROG Strix Z790-A",   1700, "LGA1700", "DDR5", "Z790"));
        all.add(mb("MSI MPG Z790 Carbon WiFi",    "MSI", "MPG Z790 Carbon",    2100, "LGA1700", "DDR5", "Z790"));
        // Intel LGA1851
        all.add(mb("Gigabyte Z890 Aorus Elite",   "Gigabyte", "Z890 Aorus Elite", 1800, "LGA1851", "DDR5", "Z890"));
        all.add(mb("ASUS ROG Strix Z890-A",       "ASUS", "ROG Strix Z890-A",   2200, "LGA1851", "DDR5", "Z890"));

        // ═══════════════════════════════════════════════════════
        // RAM — 25 (prices reflect current RAM market surge)
        // ═══════════════════════════════════════════════════════
        // DDR4 — 16GB ≈ 500 SAR
        all.add(ram("Corsair Vengeance LPX 8GB DDR4 3200", "Corsair", "CMK8GX4M1E3200C16", 250, "DDR4", "8GB",  "3200MHz"));
        all.add(ram("Corsair Vengeance LPX 16GB DDR4 3200", "Corsair", "CMK16GX4M2E3200C16", 500, "DDR4", "16GB", "3200MHz"));
        all.add(ram("G.Skill Ripjaws V 16GB DDR4 3600",    "G.Skill", "F4-3600C18D-16GVK", 550, "DDR4", "16GB", "3600MHz"));
        all.add(ram("Kingston Fury Beast 16GB DDR4 3200",  "Kingston", "KF432C16BBK2/16",  520, "DDR4", "16GB", "3200MHz"));
        all.add(ram("Corsair Vengeance LPX 32GB DDR4 3200", "Corsair", "CMK32GX4M2E3200C16", 1000, "DDR4", "32GB", "3200MHz"));
        all.add(ram("G.Skill Ripjaws V 32GB DDR4 3600",    "G.Skill", "F4-3600C18D-32GVK", 1100, "DDR4", "32GB", "3600MHz"));
        all.add(ram("TeamGroup T-Force Vulcan 32GB DDR4 3200", "TeamGroup", "TLZGD432G3200HC16CDC01", 1000, "DDR4", "32GB", "3200MHz"));
        all.add(ram("Corsair Vengeance LPX 64GB DDR4 3200", "Corsair", "CMK64GX4M2E3200C16", 1950, "DDR4", "64GB", "3200MHz"));
        // DDR5 — 16GB ≈ 1000 SAR
        all.add(ram("Kingston Fury Beast 8GB DDR5 5200",   "Kingston", "KF552C40BB-8",     500,  "DDR5", "8GB",  "5200MHz"));
        all.add(ram("Corsair Vengeance 16GB DDR5 5200",    "Corsair", "CMK16GX5M1B5200C40", 950, "DDR5", "16GB", "5200MHz"));
        all.add(ram("Corsair Vengeance 16GB DDR5 5600",    "Corsair", "CMK16GX5M1B5600C36", 1000, "DDR5", "16GB", "5600MHz"));
        all.add(ram("G.Skill Trident Z5 16GB DDR5 6000",   "G.Skill", "F5-6000J3038F16GX2", 1100, "DDR5", "16GB", "6000MHz"));
        all.add(ram("Kingston Fury Beast 16GB DDR5 6000",  "Kingston", "KF560C36BBEK2-16", 1050, "DDR5", "16GB", "6000MHz"));
        all.add(ram("TeamGroup T-Force Delta 16GB DDR5 6000", "TeamGroup", "FF3D516G6000HC30DC01", 1000, "DDR5", "16GB", "6000MHz"));
        all.add(ram("Corsair Vengeance 32GB DDR5 5600",    "Corsair", "CMK32GX5M2B5600C36", 1900, "DDR5", "32GB", "5600MHz"));
        all.add(ram("Corsair Vengeance 32GB DDR5 6000",    "Corsair", "CMK32GX5M2B6000C30", 2000, "DDR5", "32GB", "6000MHz"));
        all.add(ram("G.Skill Trident Z5 RGB 32GB DDR5 6000", "G.Skill", "F5-6000J3636F16GX2-TZ5RK", 2100, "DDR5", "32GB", "6000MHz"));
        all.add(ram("G.Skill Trident Z5 Neo 32GB DDR5 6000", "G.Skill", "F5-6000J3038F16GX2-TZ5N", 2200, "DDR5", "32GB", "6000MHz"));
        all.add(ram("Kingston Fury Beast 32GB DDR5 6400",  "Kingston", "KF564C32BBEK2-32", 2200, "DDR5", "32GB", "6400MHz"));
        all.add(ram("Corsair Dominator Titanium 32GB DDR5 7000", "Corsair", "CMP32GX5M2X7000C34", 2900, "DDR5", "32GB", "7000MHz"));
        all.add(ram("Corsair Vengeance 64GB DDR5 5600",    "Corsair", "CMK64GX5M2B5600C36", 3800, "DDR5", "64GB", "5600MHz"));
        all.add(ram("G.Skill Trident Z5 64GB DDR5 6000",   "G.Skill", "F5-6000J3040G32GX2-TZ5K", 4200, "DDR5", "64GB", "6000MHz"));
        all.add(ram("Kingston Fury Renegade 64GB DDR5 6400", "Kingston", "KF564C32RSEK2-64", 4500, "DDR5", "64GB", "6400MHz"));
        all.add(ram("Corsair Dominator Titanium 96GB DDR5 6000", "Corsair", "CMP96GX5M2B6000C30", 6000, "DDR5", "96GB", "6000MHz"));
        all.add(ram("G.Skill Trident Z5 RGB 128GB DDR5 6000", "G.Skill", "F5-6000J3636F32GX4-TZ5RK", 8000, "DDR5", "128GB", "6000MHz"));

        // ═══════════════════════════════════════════════════════
        // Storage — 25 (NAND prices also elevated)
        // ═══════════════════════════════════════════════════════
        // SATA SSDs
        all.add(storage("Crucial BX500 500GB SATA",     "Crucial", "CT500BX500SSD1", 200, "SATA SSD", "500GB"));
        all.add(storage("Samsung 870 EVO 500GB SATA",   "Samsung", "MZ-77E500B",     260, "SATA SSD", "500GB"));
        all.add(storage("Crucial BX500 1TB SATA",       "Crucial", "CT1000BX500SSD1", 380, "SATA SSD", "1TB"));
        all.add(storage("Samsung 870 EVO 1TB SATA",     "Samsung", "MZ-77E1T0B",     480, "SATA SSD", "1TB"));
        all.add(storage("Samsung 870 EVO 2TB SATA",     "Samsung", "MZ-77E2T0B",     800, "SATA SSD", "2TB"));
        // NVMe Gen3
        all.add(storage("Kingston NV2 500GB NVMe",      "Kingston", "SNV2S/500G",     250, "NVMe Gen3", "500GB"));
        all.add(storage("Kingston NV2 1TB NVMe",        "Kingston", "SNV2S/1000G",    420, "NVMe Gen3", "1TB"));
        all.add(storage("Kingston NV2 2TB NVMe",        "Kingston", "SNV2S/2000G",    800, "NVMe Gen3", "2TB"));
        all.add(storage("Crucial P3 500GB NVMe",        "Crucial", "CT500P3SSD8",    260, "NVMe Gen3", "500GB"));
        all.add(storage("Crucial P3 1TB NVMe",          "Crucial", "CT1000P3SSD8",   420, "NVMe Gen3", "1TB"));
        all.add(storage("Crucial P3 2TB NVMe",          "Crucial", "CT2000P3SSD8",   800, "NVMe Gen3", "2TB"));
        // NVMe Gen4
        all.add(storage("WD Blue SN580 500GB NVMe",     "WD", "WDS500G3B0E",        300, "NVMe Gen4", "500GB"));
        all.add(storage("WD Blue SN580 1TB NVMe",       "WD", "WDS100T3B0E",        500, "NVMe Gen4", "1TB"));
        all.add(storage("Samsung 990 EVO 1TB NVMe",     "Samsung", "MZ-V9E1T0BW",   550, "NVMe Gen4", "1TB"));
        all.add(storage("Samsung 990 Pro 1TB NVMe",     "Samsung", "MZ-V9P1T0BW",   620, "NVMe Gen4", "1TB"));
        all.add(storage("WD Black SN850X 1TB NVMe",     "WD", "WDS100T2X0E",        620, "NVMe Gen4", "1TB"));
        all.add(storage("Samsung 990 Pro 2TB NVMe",     "Samsung", "MZ-V9P2T0BW",   1100, "NVMe Gen4", "2TB"));
        all.add(storage("WD Black SN850X 2TB NVMe",     "WD", "WDS200T2X0E",        1100, "NVMe Gen4", "2TB"));
        all.add(storage("Samsung 990 Pro 4TB NVMe",     "Samsung", "MZ-V9P4T0BW",   2100, "NVMe Gen4", "4TB"));
        // NVMe Gen5
        all.add(storage("Crucial T700 1TB NVMe",        "Crucial", "CT1000T700SSD3", 900, "NVMe Gen5", "1TB"));
        all.add(storage("Crucial T700 2TB NVMe",        "Crucial", "CT2000T700SSD3", 1700, "NVMe Gen5", "2TB"));
        all.add(storage("Samsung 9100 Pro 2TB NVMe",    "Samsung", "MZ-VAP2T0BW",    1900, "NVMe Gen5", "2TB"));
        // HDDs
        all.add(storage("Seagate Barracuda 2TB HDD",    "Seagate", "ST2000DM008",    280, "HDD", "2TB"));
        all.add(storage("Seagate Barracuda 4TB HDD",    "Seagate", "ST4000DM004",    480, "HDD", "4TB"));
        all.add(storage("WD Blue 8TB HDD",              "WD", "WD80EAZZ",           850, "HDD", "8TB"));

        // ═══════════════════════════════════════════════════════
        // Power Supplies — 20
        // ═══════════════════════════════════════════════════════
        all.add(psu("Cooler Master MWE 450W Bronze",   "Cooler Master", "MWE450",       180, "450",  "80+ Bronze"));
        all.add(psu("Cooler Master MWE 550W Bronze",   "Cooler Master", "MWE550",       220, "550",  "80+ Bronze"));
        all.add(psu("Corsair CV650 650W Bronze",       "Corsair", "CV650",             300, "650",  "80+ Bronze"));
        all.add(psu("EVGA 600 BR 600W Bronze",         "EVGA", "600 BR",               320, "600",  "80+ Bronze"));
        all.add(psu("be quiet! Pure Power 12 M 650W",  "be quiet!", "BN342",           400, "650",  "80+ Gold"));
        all.add(psu("Corsair RM650e 650W Gold",        "Corsair", "RM650e",            480, "650",  "80+ Gold"));
        all.add(psu("Corsair RM750e 750W Gold",        "Corsair", "RM750e",            550, "750",  "80+ Gold"));
        all.add(psu("Corsair RM750x 750W Gold",        "Corsair", "RM750x",            620, "750",  "80+ Gold"));
        all.add(psu("Seasonic Focus GX-750 750W Gold", "Seasonic", "Focus GX-750",     600, "750",  "80+ Gold"));
        all.add(psu("Corsair RM850e 850W Gold",        "Corsair", "RM850e",            700, "850",  "80+ Gold"));
        all.add(psu("Corsair RM850x 850W Gold",        "Corsair", "RM850x",            780, "850",  "80+ Gold"));
        all.add(psu("Seasonic Focus GX-850 850W Gold", "Seasonic", "Focus GX-850",     750, "850",  "80+ Gold"));
        all.add(psu("be quiet! Straight Power 12 1000W", "be quiet!", "BN338",         950, "1000", "80+ Platinum"));
        all.add(psu("Corsair RM1000e 1000W Gold",      "Corsair", "RM1000e",           950, "1000", "80+ Gold"));
        all.add(psu("Corsair RM1000x 1000W Gold",      "Corsair", "RM1000x",           1050, "1000", "80+ Gold"));
        all.add(psu("Seasonic Prime TX-1000 1000W Titanium", "Seasonic", "Prime TX-1000", 1500, "1000", "80+ Titanium"));
        all.add(psu("Corsair HX1200 1200W Platinum",   "Corsair", "HX1200",            1500, "1200", "80+ Platinum"));
        all.add(psu("Seasonic Prime PX-1300 1300W Platinum", "Seasonic", "Prime PX-1300", 1900, "1300", "80+ Platinum"));
        all.add(psu("Corsair HX1500i 1500W Platinum",  "Corsair", "HX1500i",           2200, "1500", "80+ Platinum"));
        all.add(psu("Seasonic Prime TX-1600 1600W Titanium", "Seasonic", "Prime TX-1600", 2900, "1600", "80+ Titanium"));

        // ═══════════════════════════════════════════════════════
        // Cases — 30
        // ═══════════════════════════════════════════════════════
        // Budget mATX
        all.add(pcCase("Cooler Master MasterBox Q300L",     "Cooler Master", "MCB-Q300L", 220, "mATX Mini Tower"));
        all.add(pcCase("Deepcool Matrexx 40 3FS",           "Deepcool", "Matrexx 40",       250, "mATX Mid Tower"));
        all.add(pcCase("Cougar MX330-G",                    "Cougar", "MX330-G",            280, "ATX Mid Tower"));
        all.add(pcCase("Thermaltake Versa H18",             "Thermaltake", "CA-1J4-00S1WN-00", 260, "mATX Mini Tower"));
        // Budget ATX
        all.add(pcCase("NZXT H510",                         "NZXT", "H510",                 460, "ATX Mid Tower"));
        all.add(pcCase("Corsair 4000D Airflow",             "Corsair", "4000D",            450, "ATX Mid Tower"));
        all.add(pcCase("Fractal Design Focus G",            "Fractal", "Focus G",           380, "ATX Mid Tower"));
        all.add(pcCase("Cooler Master MasterBox TD500 Mesh V2", "Cooler Master", "TD500 Mesh V2", 500, "ATX Mid Tower"));
        // Mid
        all.add(pcCase("NZXT H5 Flow",                      "NZXT", "H5 Flow",              480, "ATX Mid Tower"));
        all.add(pcCase("Corsair 3000D Airflow",             "Corsair", "3000D",             520, "ATX Mid Tower"));
        all.add(pcCase("Fractal Design North",              "Fractal", "North",             680, "ATX Mid Tower"));
        all.add(pcCase("Lian Li Lancool 216",               "Lian Li", "Lancool 216",       620, "ATX Mid Tower"));
        all.add(pcCase("Corsair 5000D Airflow",             "Corsair", "5000D",             820, "ATX Mid Tower"));
        all.add(pcCase("Lian Li PC-O11 Dynamic",            "Lian Li", "PC-O11DW",          600, "ATX Mid Tower"));
        all.add(pcCase("NZXT H7 Flow",                      "NZXT", "H7 Flow",              680, "ATX Mid Tower"));
        all.add(pcCase("Fractal Design Pop Air RGB",        "Fractal", "Pop Air",            580, "ATX Mid Tower"));
        all.add(pcCase("be quiet! Pure Base 500DX",         "be quiet!", "Pure Base 500DX", 620, "ATX Mid Tower"));
        // Premium
        all.add(pcCase("Fractal Design Meshify 2",          "Fractal", "Meshify 2",         950, "ATX Mid Tower"));
        all.add(pcCase("Lian Li O11 Dynamic EVO",           "Lian Li", "O11 Dynamic EVO",  1000, "ATX Mid Tower"));
        all.add(pcCase("Corsair 7000D Airflow",             "Corsair", "7000D",            1200, "Full Tower"));
        all.add(pcCase("Phanteks Eclipse G500A",            "Phanteks", "Eclipse G500A",  700, "ATX Mid Tower"));
        all.add(pcCase("Phanteks NV5",                      "Phanteks", "NV5",             950, "ATX Mid Tower"));
        all.add(pcCase("Corsair 5000X RGB",                 "Corsair", "5000X",           1050, "ATX Mid Tower"));
        all.add(pcCase("Lian Li V3000 Plus",                "Lian Li", "V3000 Plus",      2200, "Full Tower"));
        // SFF / mITX
        all.add(pcCase("Cooler Master NR200P",              "Cooler Master", "NR200P",      700, "Mini-ITX"));
        all.add(pcCase("Lian Li A4-H2O",                    "Lian Li", "A4-H2O",            950, "Mini-ITX"));
        all.add(pcCase("Fractal Design Ridge",              "Fractal", "Ridge",             800, "Mini-ITX"));
        all.add(pcCase("Cooler Master MasterBox NR200",     "Cooler Master", "NR200",       550, "Mini-ITX"));
        all.add(pcCase("NZXT H1 V2",                        "NZXT", "H1 V2",                1100, "Mini-ITX"));

        repo.saveAll(all);
        log.info("Seed complete. {} components available.", repo.count());
    }

    // ─── Factory helpers ───

    private Component cpu(String name, String brand, String model, int price,
                          String socket, String cores, String tdp) {
        return make(name, brand, model, price, ComponentCategory.CPU,
                "socket", socket, "cores", cores, "tdp", tdp);
    }

    private Component gpu(String name, String brand, String model, int price,
                          String tdp, String vram) {
        return make(name, brand, model, price, ComponentCategory.GPU,
                "tdp", tdp, "vram", vram);
    }

    private Component mb(String name, String brand, String model, int price,
                         String socket, String memoryType, String chipset) {
        return make(name, brand, model, price, ComponentCategory.MOTHERBOARD,
                "socket", socket, "memory_type", memoryType, "chipset", chipset);
    }

    private Component ram(String name, String brand, String model, int price,
                          String type, String capacity, String speed) {
        return make(name, brand, model, price, ComponentCategory.RAM,
                "type", type, "capacity", capacity, "speed", speed);
    }

    private Component storage(String name, String brand, String model, int price,
                              String type, String capacity) {
        return make(name, brand, model, price, ComponentCategory.STORAGE,
                "type", type, "capacity", capacity);
    }

    private Component psu(String name, String brand, String model, int price,
                          String wattage, String efficiency) {
        return make(name, brand, model, price, ComponentCategory.POWER_SUPPLY,
                "wattage", wattage, "efficiency", efficiency);
    }

    private Component pcCase(String name, String brand, String model, int price,
                             String formFactor) {
        return make(name, brand, model, price, ComponentCategory.CASE,
                "form_factor", formFactor);
    }

    private Component make(String name, String brand, String model, int price,
                           ComponentCategory cat, String... specsKv) {
        Map<String, String> specs = new HashMap<>();
        for (int i = 0; i + 1 < specsKv.length; i += 2) {
            specs.put(specsKv[i], specsKv[i + 1]);
        }
        return Component.builder()
                .name(name)
                .brand(brand)
                .model(model)
                .category(cat)
                .specs(specs)
                .imageUrl(CATEGORY_IMAGES.get(cat))
                .fallbackPrice(BigDecimal.valueOf(price))
                .active(true)
                .build();
    }
}