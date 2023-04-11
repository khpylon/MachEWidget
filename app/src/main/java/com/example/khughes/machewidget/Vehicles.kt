package com.example.khughes.machewidget

open class Vehicle(val VIN: String) {

    open val horizontalDrawables: MutableMap<String, Int> = mutableMapOf()
    open val verticalDrawables: MutableMap<String, Int> = mutableMapOf()
    open val layoutID = R.layout.mache_widget
    open val offsetPositions = arrayOf(0, 0)
    open val logoID = R.drawable.generic_logo
    open val name = ""

    companion object {

        private const val WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1
        private const val WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3
        private const val WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV = "3FM"
        private const val WORLD_MANUFACTURING_IDENTIFIER_MEXICO_TRUCK = "3FT"
        private const val WORLD_MANUFACTURING_IDENTIFIER_GERMANY = "WF0"
        private const val WORLD_MANUFACTURING_IDENTIFIER_USA_CAR = "1FA"
        private const val WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK = "1FT"
        private const val WORLD_MANUFACTURING_IDENTIFIER_USA_MPV = "1FM"

        private const val NA_LINE_SERIES_START_INDEX = 5 - 1
        private const val NA_LINE_SERIES_END_INDEX = 7

        private const val NA_LINE_SERIES_MACHE_SELECT_RWD = "K1R" // select RWD
        private const val NA_LINE_SERIES_MACHE_SELECT_AWD = "K1S" // select RWD (AWD?
        private const val NA_LINE_SERIES_MACHE_CAROUTE1_RWD = "K2R" // Route 1 RWD
        private const val NA_LINE_SERIES_MACHE_PREMIUM_RWD = "K3R" // Premium RWD
        private const val NA_LINE_SERIES_MACHE_PREMIUM_AWD = "K3S" // Premium AWD?
        private const val NA_LINE_SERIES_MACHE_GT_RWD = "K4S" // GT AWD

        private const val NA_LINE_SERIES_F150_REGULAR_4X2 = "F1C" // 4x2 chassis, regular cab
        private const val NA_LINE_SERIES_F150_REGULAR_4X4 = "F1E" // 4x4 chassis, regular cab
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X2 = "W1C" // 4x2, SuperCrew
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X4 = "W1E" // 4x4, superCrew
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR = "W1R" // 4x4, SuperCrew, Raptor
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X4_POLICE = "W1P" // 4x4, SuperCrew, Police
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV = "W1S" // 4x2, SuperCrew, SSV (Special Service Vehicle), government
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV = "W1T" // 4x4, superCrew, SSV (Special Service Vehicle), government
        private const val NA_LINE_SERIES_F150_SUPERCAB_4X2 = "X1C" // 4x2, SuperCab
        private const val NA_LINE_SERIES_F150_SUPERCAB_4X4 = "X1E" // 4x4, SuperCab

        private const val NA_LINE_SERIES_F250_REGULAR_4X2 = "F2A" // 4x2 chassis, regular cab
        private const val NA_LINE_SERIES_F250_REGULAR_4X4 = "F2B" // 4x4 chassis, regular cab
        private const val NA_LINE_SERIES_F250_CREWCAB_4X2 = "W2A" // 4x2, CrewCab
        private const val NA_LINE_SERIES_F250_CREWCAB_4X4 = "W2B" // 4x4, CrewCab
        private const val NA_LINE_SERIES_F250_SUPERCAB_4X2 = "X2A" // 4x2, SuperCab
        private const val NA_LINE_SERIES_F250_SUPERCAB_4X4 = "X2B" // 4x4, SuperCab

        private const val NA_LINE_SERIES_F350_REGULAR_SRW_4X2_A = "F3A"
        private const val NA_LINE_SERIES_F350_REGULAR_SRW_4X4_B = "F3B"
        private const val NA_LINE_SERIES_F350_REGULAR_DRW_4X2_C = "F3C"
        private const val NA_LINE_SERIES_F350_REGULAR_DRW_4X4_D = "F3D"
        private const val NA_LINE_SERIES_F350_REGULAR_SRW_4X2_E = "F3E"
        private const val NA_LINE_SERIES_F350_REGULAR_SRW_4X4_F = "F3F"
        private const val NA_LINE_SERIES_F350_REGULAR_DRW_4X2_G = "F3G"
        private const val NA_LINE_SERIES_F350_REGULAR_DRW_4X4_H = "F3H"
        private const val NA_LINE_SERIES_F350_CREWCAB_SRW_4X2_A = "W3A"
        private const val NA_LINE_SERIES_F350_CREWCAB_SRW_4X4_B = "W3B"
        private const val NA_LINE_SERIES_F350_CREWCAB_DRW_4X2_C = "W3C"
        private const val NA_LINE_SERIES_F350_CREWCAB_DRW_4X4_D = "W3D"
        private const val NA_LINE_SERIES_F350_CREWCAB_SRW_4X2_E = "W3E"
        private const val NA_LINE_SERIES_F350_CREWCAB_SRW_4X4_F = "W3F"
        private const val NA_LINE_SERIES_F350_CREWCAB_DRW_4X2_G = "W3G"
        private const val NA_LINE_SERIES_F350_CREWCAB_DRW_4X4_H = "W3H"
        private const val NA_LINE_SERIES_F350_SUPERCAB_SRW_4X2_A = "X3A"
        private const val NA_LINE_SERIES_F350_SUPERCAB_SRW_4X4_B = "X3B"
        private const val NA_LINE_SERIES_F350_SUPERCAB_DRW_4X2_C = "X3C"
        private const val NA_LINE_SERIES_F350_SUPERCAB_DRW_4X4_D = "X3D"
        private const val NA_LINE_SERIES_F350_SUPERCAB_SRW_4X2_E = "X3E"
        private const val NA_LINE_SERIES_F350_SUPERCAB_SRW_4X4_F = "X3F"
        private const val NA_LINE_SERIES_F350_SUPERCAB_DRW_4X2_G = "X3G"
        private const val NA_LINE_SERIES_F350_SUPERCAB_DRW_4X4_H = "X3H"

        private const val NA_LINE_SERIES_BRONCO_BASE_2DOOR_4X4 = "E5A" //
        private const val NA_LINE_SERIES_BRONCO_BASE_4DOOR_4X4 = "E5B" //
        private const val NA_LINE_SERIES_BRONCO_BASE_2DOOR_AWD = "E5C" //
        private const val NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD = "E5D" //
        private const val NA_LINE_SERIES_BRONCO_FE_4DOOR_AWD = "E5E" //
        private const val NA_LINE_SERIES_BRONCO_FE_2DOOR_AWD = "E5F" //
        private const val NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR = "E5J" //

        private const val NA_LINE_SERIES_BRONCOSPORT_BASE_4x4 = "R9A"
        private const val NA_LINE_SERIES_BRONCOSPORT_BIGBEND_4x4 = "R9B"
        private const val NA_LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4 = "R9C"
        private const val NA_LINE_SERIES_BRONCOSPORT_BADLANDS_4x4 = "R9D"
        private const val NA_LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4 = "R9E"

        private const val NA_LINE_SERIES_EXPLORER_BASE_RWD = "K7B"
        private const val NA_LINE_SERIES_EXPLORER_XLT_RWD = "K7D"
        private const val NA_LINE_SERIES_EXPLORER_LIMITED_RWD = "K7F"
        private const val NA_LINE_SERIES_EXPLORER_PLATINUM_RWD = "K7H"
        private const val NA_LINE_SERIES_EXPLORER_KING_RWD = "K7L"
        private const val NA_LINE_SERIES_EXPLORER_ST_RWD = "K7G"
        private const val NA_LINE_SERIES_EXPLORER_STLINE_RWD = "K7K"
        private const val NA_LINE_SERIES_EXPLORER_POLICE = "K8A"
        private const val NA_LINE_SERIES_EXPLORER_BASE_4WD = "K8B"
        private const val NA_LINE_SERIES_EXPLORER_XLT_4WD = "K8D"
        private const val NA_LINE_SERIES_EXPLORER_LIMITED_4WD = "K8F"
        private const val NA_LINE_SERIES_EXPLORER_ST_4WD = "K8G"
        private const val NA_LINE_SERIES_EXPLORER_PLATINUM_4WD = "K8H"
        private const val NA_LINE_SERIES_EXPLORER_KING_4WD = "K8L"
        private const val NA_LINE_SERIES_EXPLORER_STLINE_4WD = "K8K"
        private const val NA_LINE_SERIES_EXPLORER_TIMBERLINE_4WD = "K8J"

        private const val NA_LINE_SERIES_ESCAPE_S_RWD = "U0F"
        private const val NA_LINE_SERIES_ESCAPE_SE_RWD = "U0G"
        private const val NA_LINE_SERIES_ESCAPE_SEL_RWD = "U0H"
        private const val NA_LINE_SERIES_ESCAPE_SE_FHEV_RWD = "U0B"
        private const val NA_LINE_SERIES_ESCAPE_SEL_FHEV_RWD = "U0C"
        private const val NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD = "U0D"
        private const val NA_LINE_SERIES_ESCAPE_SE_PHEV_RWD = "U0E"
        private const val NA_LINE_SERIES_ESCAPE_SEL_PHEV_RWD = "U0K"
        private const val NA_LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD = "U0L"
        private const val NA_LINE_SERIES_ESCAPE_S_4WD = "U9F"
        private const val NA_LINE_SERIES_ESCAPE_SE_4WD = "U9G"
        private const val NA_LINE_SERIES_ESCAPE_SEL_4WD = "U9H"
        private const val NA_LINE_SERIES_ESCAPE_TITANIUM_4WD = "U9J"
        private const val NA_LINE_SERIES_ESCAPE_SE_FHEV_4WD = "U9B"
        private const val NA_LINE_SERIES_ESCAPE_SEL_FHEV_4WD = "U9C"
        private const val NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD = "U9D"

        private const val NA_LINE_SERIES_EDGE_ST_AWD = "K4A"
        private const val NA_LINE_SERIES_EDGE_SE_AWD = "K4G"
        private const val NA_LINE_SERIES_EDGE_SEL_AWD = "K4J"
        private const val NA_LINE_SERIES_EDGE_TITANIUM_AWD = "K4K"

        private const val NA_LINE_SERIES_EXPEDITION_MAX_XL_4x2 = "K1F"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_XL_4x4 = "K1G"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x2 = "K1H"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x4 = "K1J"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x2 = "K1N"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x4 = "K1P"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x2 = "K1K"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x4 = "K2A"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x2 = "K1L"
        private const val NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x4 = "K1M"
        private const val NA_LINE_SERIES_EXPEDITION_XL_4x2 = "U1F"
        private const val NA_LINE_SERIES_EXPEDITION_XL_4x4 = "U1G"
        private const val NA_LINE_SERIES_EXPEDITION_XLT_4x2 = "U1H"
        private const val NA_LINE_SERIES_EXPEDITION_XLT_4x4 = "U1J"
        private const val NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x2 = "U1N"
        private const val NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x4 = "U1P"
        private const val NA_LINE_SERIES_EXPEDITION_LIMITED_4x2 = "U1K"
        private const val NA_LINE_SERIES_EXPEDITION_LIMITED_4x4 = "U2A"
        private const val NA_LINE_SERIES_EXPEDITION_PLATINUM_4x2 = "U1L"
        private const val NA_LINE_SERIES_EXPEDITION_PLATINUM_4x4 = "U1M"
        private const val NA_LINE_SERIES_EXPEDITION_TIMBERLINE_4x4 = "U1R"

        private const val NA_LINE_SERIES_RANGER_SUPERCAB_4x2 = "R1E"
        private const val NA_LINE_SERIES_RANGER_SUPERCAB_4x4 = "R1F"
        private const val NA_LINE_SERIES_RANGER_SUPERCREW_4x2 = "R4E"
        private const val NA_LINE_SERIES_RANGER_SUPERCREW_4x4 = "R4F"

        private const val NA_LINE_SERIES_MAVERICK_SUPERCREW_4x2 = "W8E"
        private const val NA_LINE_SERIES_MAVERICK_SUPERCREW_4x4 = "W8F"

        private const val NA_LINE_SERIES_MUSTANG_GT_COUPE = "P8C"
        private const val NA_LINE_SERIES_MUSTANG_GT_CONVERTABLE = "P8F"
        private const val NA_LINE_SERIES_MUSTANG_ECOBOOST_COUPE = "P8T"
        private const val NA_LINE_SERIES_MUSTANG_ECOBOOST_CONVERTABLE = "P8U"
        private const val NA_LINE_SERIES_MUSTANG_MACH1_COUPE = "P8R"
        private const val NA_LINE_SERIES_MUSTANG_SHELBY_GT600_COUPE = "P8S"

        private const val NA_LINE_SERIES_FOCUS_S_SEDAN = "P3E"
        private const val NA_LINE_SERIES_FOCUS_SE_SEDAN = "P3F"
        private const val NA_LINE_SERIES_FOCUS_SEL_SEDAN = "P3H"
        private const val NA_LINE_SERIES_FOCUS_TITANIUM_SEDAN = "P3J"
        private const val NA_LINE_SERIES_FOCUS_SE_HATCH = "P3K"
        private const val NA_LINE_SERIES_FOCUS_SEL_HATCH = "P3M"
        private const val NA_LINE_SERIES_FOCUS_TITANIUM_HATCH = "P3N"
        private const val NA_LINE_SERIES_FOCUS_ST_HATCH = "P3L"
        private const val NA_LINE_SERIES_FOCUS_BEV_HATCH = "P3R"
        private const val NA_LINE_SERIES_FOCUS_RS_HATCH = "P3T"

        private const val EURO_LINE_SERIES_START_INDEX = 7 - 1
        private const val EURO_LINE_SERIES_END_INDEX = 9

        private const val EURO_LINE_SERIES_KUGA = "WPM"
        private const val EURO_LINE_SERIES_PUMA = "ERK"
        private const val EURO_LINE_SERIES_FOCUS = "GCH"

        private fun isMachE(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val macheLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_MACHE_SELECT_RWD,
                NA_LINE_SERIES_MACHE_SELECT_RWD,
                NA_LINE_SERIES_MACHE_SELECT_AWD,
                NA_LINE_SERIES_MACHE_CAROUTE1_RWD,
                NA_LINE_SERIES_MACHE_PREMIUM_RWD,
                NA_LINE_SERIES_MACHE_PREMIUM_AWD,
                NA_LINE_SERIES_MACHE_GT_RWD,
            )
            val lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return (wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY || wmi == WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV)
                    && macheLineSeries.contains(lineSeries)
        }

        private fun isF150RegularCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f150RegularCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F150_REGULAR_4X2,
                NA_LINE_SERIES_F150_REGULAR_4X4,
            )
            return f150RegularCabsLineSeries.contains(lineSeries)
        }

        private fun isF150SuperCrew(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f150SuperCrewsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F150_SUPERCREW_4X2,
                NA_LINE_SERIES_F150_SUPERCREW_4X4,
                NA_LINE_SERIES_F150_SUPERCREW_4X4_POLICE,
                NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV,
                NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV,
            )
            return f150SuperCrewsLineSeries.contains(lineSeries)
        }

        private fun isF150Raptor(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return lineSeries == NA_LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR
        }

        private fun isF150SuperCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f150SuperCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F150_SUPERCAB_4X2,
                NA_LINE_SERIES_F150_SUPERCAB_4X4,
            )
            return f150SuperCabsLineSeries.contains(lineSeries)
        }

        private fun isF150(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK &&
                    (isF150RegularCab(VIN) || isF150SuperCab(VIN) || isF150SuperCrew(VIN) || isF150Raptor(
                        VIN
                    ))
        }

        private fun isF250RegularCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f250RegularCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F250_REGULAR_4X2,
                NA_LINE_SERIES_F250_REGULAR_4X4,
            )
            return f250RegularCabsLineSeries.contains(lineSeries)
        }

        private fun isF250CrewCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f250CrewCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F250_CREWCAB_4X2,
                NA_LINE_SERIES_F250_CREWCAB_4X4,
            )
            return f250CrewCabsLineSeries.contains(lineSeries)
        }

        private fun isF250SuperCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val f250SuperCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_F250_SUPERCAB_4X2,
                NA_LINE_SERIES_F250_SUPERCAB_4X4,
            )
            return f250SuperCabsLineSeries.contains(lineSeries)
        }

        private fun isF250(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK &&
                    (isF250RegularCab(VIN) || isF250SuperCab(VIN) || isF250CrewCab(VIN))
        }

        private fun isF350RegularCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return setOf(
                NA_LINE_SERIES_F350_REGULAR_SRW_4X2_A,
                NA_LINE_SERIES_F350_REGULAR_SRW_4X4_B,
                NA_LINE_SERIES_F350_REGULAR_DRW_4X2_C,
                NA_LINE_SERIES_F350_REGULAR_DRW_4X4_D,
                NA_LINE_SERIES_F350_REGULAR_SRW_4X2_E,
                NA_LINE_SERIES_F350_REGULAR_SRW_4X4_F,
                NA_LINE_SERIES_F350_REGULAR_DRW_4X2_G,
                NA_LINE_SERIES_F350_REGULAR_DRW_4X4_H,
            ).contains(lineSeries)
        }

        private fun isF350CrewCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return setOf(
                NA_LINE_SERIES_F350_CREWCAB_SRW_4X2_A,
                NA_LINE_SERIES_F350_CREWCAB_SRW_4X4_B,
                NA_LINE_SERIES_F350_CREWCAB_DRW_4X2_C,
                NA_LINE_SERIES_F350_CREWCAB_DRW_4X4_D,
                NA_LINE_SERIES_F350_CREWCAB_SRW_4X2_E,
                NA_LINE_SERIES_F350_CREWCAB_SRW_4X4_F,
                NA_LINE_SERIES_F350_CREWCAB_DRW_4X2_G,
                NA_LINE_SERIES_F350_CREWCAB_DRW_4X4_H,
            ).contains(lineSeries)
        }

        private fun isF350SuperCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return setOf(
                NA_LINE_SERIES_F350_SUPERCAB_SRW_4X2_A,
                NA_LINE_SERIES_F350_SUPERCAB_SRW_4X4_B,
                NA_LINE_SERIES_F350_SUPERCAB_DRW_4X2_C,
                NA_LINE_SERIES_F350_SUPERCAB_DRW_4X4_D,
                NA_LINE_SERIES_F350_SUPERCAB_SRW_4X2_E,
                NA_LINE_SERIES_F350_SUPERCAB_SRW_4X4_F,
                NA_LINE_SERIES_F350_SUPERCAB_DRW_4X2_G,
                NA_LINE_SERIES_F350_SUPERCAB_DRW_4X4_H,
            ).contains(lineSeries)
        }

        private fun isF350(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK &&
                    (isF350RegularCab(VIN) || isF350SuperCab(VIN) || isF350CrewCab(VIN) )
        }

        private val explorerLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_EXPLORER_BASE_RWD,
            NA_LINE_SERIES_EXPLORER_XLT_RWD,
            NA_LINE_SERIES_EXPLORER_LIMITED_RWD,
            NA_LINE_SERIES_EXPLORER_PLATINUM_RWD,
            NA_LINE_SERIES_EXPLORER_KING_RWD,
            NA_LINE_SERIES_EXPLORER_ST_RWD,
            NA_LINE_SERIES_EXPLORER_STLINE_RWD,
            NA_LINE_SERIES_EXPLORER_POLICE,
            NA_LINE_SERIES_EXPLORER_BASE_4WD,
            NA_LINE_SERIES_EXPLORER_XLT_4WD,
            NA_LINE_SERIES_EXPLORER_LIMITED_4WD,
            NA_LINE_SERIES_EXPLORER_ST_4WD,
            NA_LINE_SERIES_EXPLORER_PLATINUM_4WD,
            NA_LINE_SERIES_EXPLORER_KING_4WD,
            NA_LINE_SERIES_EXPLORER_STLINE_4WD,
            NA_LINE_SERIES_EXPLORER_TIMBERLINE_4WD,
        )

        private fun isExplorer(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && explorerLineSeries.contains(lineSeries)
        }

        private val broncoLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_BRONCO_BASE_2DOOR_4X4,
            NA_LINE_SERIES_BRONCO_BASE_4DOOR_4X4,
            NA_LINE_SERIES_BRONCO_BASE_2DOOR_AWD,
            NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD,
            NA_LINE_SERIES_BRONCO_FE_4DOOR_AWD,
            NA_LINE_SERIES_BRONCO_FE_2DOOR_AWD,
            NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR,
        )

        private fun isBronco(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && broncoLineSeries.contains(lineSeries)
        }

        private val broncoSportLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_BRONCOSPORT_BASE_4x4,
            NA_LINE_SERIES_BRONCOSPORT_BIGBEND_4x4,
            NA_LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4,
            NA_LINE_SERIES_BRONCOSPORT_BADLANDS_4x4,
            NA_LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4,
        )

        private fun isBroncoSport(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && broncoSportLineSeries.contains(lineSeries)
        }

        private val escapeLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_ESCAPE_S_RWD,
            NA_LINE_SERIES_ESCAPE_SE_RWD,
            NA_LINE_SERIES_ESCAPE_SEL_RWD,
            NA_LINE_SERIES_ESCAPE_SE_FHEV_RWD,
            NA_LINE_SERIES_ESCAPE_SEL_FHEV_RWD,
            NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD,
            NA_LINE_SERIES_ESCAPE_SE_PHEV_RWD,
            NA_LINE_SERIES_ESCAPE_SEL_PHEV_RWD,
            NA_LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD,
            NA_LINE_SERIES_ESCAPE_S_4WD,
            NA_LINE_SERIES_ESCAPE_SE_4WD,
            NA_LINE_SERIES_ESCAPE_SEL_4WD,
            NA_LINE_SERIES_ESCAPE_TITANIUM_4WD,
            NA_LINE_SERIES_ESCAPE_SE_FHEV_4WD,
            NA_LINE_SERIES_ESCAPE_SEL_FHEV_4WD,
            NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD,
        )

        private fun isEscape(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && escapeLineSeries.contains(lineSeries)
        }

        private val edgeLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_EDGE_ST_AWD,
            NA_LINE_SERIES_EDGE_SE_AWD,
            NA_LINE_SERIES_EDGE_SEL_AWD,
            NA_LINE_SERIES_EDGE_TITANIUM_AWD,
        )

        private fun isEdge(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && edgeLineSeries.contains(lineSeries)
        }

        private fun isFocus(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val focusLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_FOCUS_S_SEDAN,
                NA_LINE_SERIES_FOCUS_SE_SEDAN,
                NA_LINE_SERIES_FOCUS_SEL_SEDAN,
                NA_LINE_SERIES_FOCUS_TITANIUM_SEDAN,
                NA_LINE_SERIES_FOCUS_SE_HATCH,
                NA_LINE_SERIES_FOCUS_SEL_HATCH,
                NA_LINE_SERIES_FOCUS_TITANIUM_HATCH,
                NA_LINE_SERIES_FOCUS_ST_HATCH,
                NA_LINE_SERIES_FOCUS_BEV_HATCH,
                NA_LINE_SERIES_FOCUS_RS_HATCH,
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_CAR &&
                    focusLineSeries.contains(lineSeries)
        }

        private val expeditionLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_EXPEDITION_MAX_XL_4x2,
            NA_LINE_SERIES_EXPEDITION_MAX_XL_4x4,
            NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x2,
            NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x4,
            NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x2,
            NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x4,
            NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x2,
            NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x4,
            NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x2,
            NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x4,
            NA_LINE_SERIES_EXPEDITION_XL_4x2,
            NA_LINE_SERIES_EXPEDITION_XL_4x4,
            NA_LINE_SERIES_EXPEDITION_XLT_4x2,
            NA_LINE_SERIES_EXPEDITION_XLT_4x4,
            NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x2,
            NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x4,
            NA_LINE_SERIES_EXPEDITION_LIMITED_4x2,
            NA_LINE_SERIES_EXPEDITION_LIMITED_4x4,
            NA_LINE_SERIES_EXPEDITION_PLATINUM_4x2,
            NA_LINE_SERIES_EXPEDITION_PLATINUM_4x4,
            NA_LINE_SERIES_EXPEDITION_TIMBERLINE_4x4,
        )

        private fun isRangerSuperCrew(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val rangerSuperCrewsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_RANGER_SUPERCREW_4x2,
                NA_LINE_SERIES_RANGER_SUPERCREW_4x4,
            )
            return rangerSuperCrewsLineSeries.contains(lineSeries)
        }

        private fun isRangerSuperCab(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            val rangerSuperCabsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_RANGER_SUPERCAB_4x2,
                NA_LINE_SERIES_RANGER_SUPERCAB_4x4,
            )
            return rangerSuperCabsLineSeries.contains(lineSeries)
        }

        private fun isRanger(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK &&
                    (isRangerSuperCab(VIN) || isRangerSuperCrew(VIN))
        }

        private fun isMaverick(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val maverickSuperCrewsLineSeries: Set<String> = setOf(
                NA_LINE_SERIES_MAVERICK_SUPERCREW_4x2,
                NA_LINE_SERIES_MAVERICK_SUPERCREW_4x4,
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_MEXICO_TRUCK &&
                    maverickSuperCrewsLineSeries.contains(lineSeries)
        }

        private fun isMustang(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_CAR
                    && mustangLineSeries.contains(lineSeries)
        }

        private val mustangLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_MUSTANG_GT_COUPE,
            NA_LINE_SERIES_MUSTANG_GT_CONVERTABLE,
            NA_LINE_SERIES_MUSTANG_ECOBOOST_COUPE,
            NA_LINE_SERIES_MUSTANG_ECOBOOST_CONVERTABLE,
            NA_LINE_SERIES_MUSTANG_MACH1_COUPE,
            NA_LINE_SERIES_MUSTANG_SHELBY_GT600_COUPE
        )

        private fun isExpedition(VIN: String): Boolean {
            if (VIN.length < NA_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && expeditionLineSeries.contains(lineSeries)
        }


        private fun isKuga(VIN: String): Boolean {
            if (VIN.length < EURO_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY && lineSeries == EURO_LINE_SERIES_KUGA
        }

        private fun isPuma(VIN: String): Boolean {
            if (VIN.length < EURO_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY && lineSeries == EURO_LINE_SERIES_PUMA
        }

        private fun isEUFocus(VIN: String): Boolean {
            if (VIN.length < EURO_LINE_SERIES_END_INDEX) return false
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY && lineSeries == EURO_LINE_SERIES_FOCUS
        }

        // Check to see if we recognize a VIN in general
        @JvmStatic
        fun isVINRecognized(VIN: String): Boolean =
            isMachE(VIN) || isF150(VIN) || isF250(VIN) || isF350(VIN)
            || isBronco(VIN) || isBroncoSport(VIN) || isExplorer(VIN)
            || isEscape(VIN) || isEdge(VIN) || isFocus(VIN)
            || isExpedition(VIN) || isRanger(VIN) || isMaverick(VIN)
            || isMustang(VIN) || isKuga(VIN) || isPuma(VIN) || isEUFocus(VIN)

        @JvmStatic
        fun getVehicle(VIN: String?): Vehicle {
            // Check all the inherited classes first
            if (VIN == null || VIN == "") return MachE("")
            if (isMachE(VIN)) return MachE(VIN)
            if (isBroncoSport(VIN)) return BroncoSport(VIN)
            if (isExpedition(VIN)) return Expedition(VIN)
            if (isMustang(VIN)) return Mustang(VIN)
            if (isKuga(VIN)) return Kuga(VIN)
            if (isPuma(VIN)) return Puma(VIN)
            if (isFocus(VIN) || isEUFocus(VIN)) return Focus(VIN)

            // Next check for F-150 variants
            if (isF150RegularCab(VIN)) return F150RegularCab(VIN)
            if (isF150SuperCab(VIN)) return F150SuperCab(VIN)
            if (isF150SuperCrew(VIN) || isF150Raptor(VIN)) return F150SuperCrew(VIN)

            // Next check for F-250 variants
            if (isF250RegularCab(VIN)) return F250RegularCab(VIN)
            if (isF250SuperCab(VIN)) return F250SuperCab(VIN)
            if (isF250CrewCab(VIN)) return F250CrewCab(VIN)

            // How about F-350 variants?
            if (isF350RegularCab(VIN)) return F250RegularCab(VIN)
            if (isF350SuperCab(VIN)) return F250SuperCab(VIN)
            if (isF350CrewCab(VIN)) return F250CrewCab(VIN)

            // Ranger variants?
            if (isRangerSuperCrew(VIN)) return RangerSuperCrew(VIN)
            if (isRangerSuperCab(VIN)) return RangerSuperCab(VIN)

            // Check everything else
            if (isBronco(VIN)) return Bronco(VIN)
            if (isEdge(VIN)) return Edge(VIN)
            if (isEscape(VIN)) return Escape(VIN)
            if (isExplorer(VIN)) return Explorer(VIN)
            if (isMaverick(VIN)) return Maverick(VIN)

            // If none of the above, then see if it's a "truck"
            if (VIN.startsWith(WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK)) return F150RegularCab(VIN)

            // Default to the Mach-E
            return MachE(VIN)
        }

        // Model year decoder
        private val modelYears: Map<String, Int> = mapOf(
            "G" to 2016,
            "H" to 2017,
            "J" to 2018,
            "K" to 2019,
            "L" to 2020,
            "M" to 2021,
            "N" to 2022,
            "P" to 2023,
            "R" to 2024,
            "S" to 2025,
            "T" to 2026,
            "V" to 2027,
            "W" to 2028,
            "X" to 2029,
            "Y" to 2030,
        )
        private const val NA_MODEL_YEAR_START_INDEX = 10 - 1
        private const val NA_MODEL_YEAR_END_INDEX = 10
        private const val EURO_MODEL_YEAR_START_INDEX = 11 - 1
        private const val EURO_MODEL_YEAR_END_INDEX = 11

        @JvmStatic
        fun getModelYear(VIN: String?): Int {
            VIN?.let {
                val wmi = VIN.substring(
                    WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                    WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
                )
                val vehicleYearCode =
                    if (wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY) VIN.substring(
                        EURO_MODEL_YEAR_START_INDEX,
                        EURO_MODEL_YEAR_END_INDEX
                    )
                    else VIN.substring(NA_MODEL_YEAR_START_INDEX, NA_MODEL_YEAR_END_INDEX)
                return modelYears.getOrDefault(vehicleYearCode, 0)
            }
            return 0
        }

        const val WIREFRAME = "wireframe"
        const val HOOD = "hood"
        const val TAILGATE = "tailgate_open"
        const val LEFT_FRONT_DOOR = "lfdoor_open"
        const val RIGHT_FRONT_DOOR = "rfdoor_open"
        const val LEFT_REAR_DOOR = "lrdoor_open"
        const val RIGHT_REAR_DOOR = "rrdoor_open"
        const val LEFT_FRONT_WINDOW = "lfwindow_open"
        const val RIGHT_FRONT_WINDOW = "rfwindow_open"
        const val LEFT_REAR_WINDOW = "lrwindow_open"
        const val RIGHT_REAR_WINDOW = "rrwindow_open"
        const val BODY_PRIMARY = "body1st"
        const val BODY_SECONDARY = "body2nd"
    }
}

class MachE(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.mache_wireframe_vert,
        HOOD to R.drawable.mache_frunk_vert,
        TAILGATE to R.drawable.mache_hatch_vert,
        LEFT_FRONT_DOOR to R.drawable.mache_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.mache_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.mache_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.mache_rrdoor_vert,
        BODY_PRIMARY to R.drawable.mache_primary_vert,
        BODY_SECONDARY to R.drawable.mache_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.mache_wireframe_horz,
        HOOD to R.drawable.mache_frunk_horz,
        TAILGATE to R.drawable.mache_hatch_horz,
        LEFT_FRONT_DOOR to R.drawable.mache_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.mache_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.mache_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.mache_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.mache_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.mache_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.mache_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.mache_rrwindow_horz,
        BODY_PRIMARY to R.drawable.mache_primary_horz,
        BODY_SECONDARY to R.drawable.mache_secondary_horz,
    )

    override val layoutID = R.layout.mache_widget
    override val offsetPositions = arrayOf(352, 288)
    override val logoID = R.drawable.mache_logo
    override val name = "Mustang Mach-E"
}

open class F150(VIN: String) : Vehicle(VIN) {
    override val layoutID = R.layout.f150_widget
    override val offsetPositions = arrayOf(344, 220)
    override val logoID = R.drawable.ford_f150_logo
    override val name = "F-150"
}

class F150RegularCab(VIN: String) : F150(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.regularcab_wireframe_vert,
        HOOD to R.drawable.regularcab_hood_vert,
        TAILGATE to R.drawable.regularcab_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.regularcab_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.regularcab_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        BODY_PRIMARY to R.drawable.regularcab_primary_vert,
        BODY_SECONDARY to R.drawable.regularcab_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.regularcab_wireframe_horz,
        HOOD to R.drawable.regularcab_hood_horz,
        TAILGATE to R.drawable.regularcab_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.regularcab_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.regularcab_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        LEFT_FRONT_WINDOW to R.drawable.regularcab_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.regularcab_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.filler,
        RIGHT_REAR_WINDOW to R.drawable.filler,
        BODY_PRIMARY to R.drawable.regularcab_primary_horz,
        BODY_SECONDARY to R.drawable.regularcab_secondary_horz,
    )

    override val name = "F-150 Regular Cab"
}

open class F150SuperCab(VIN: String) : F150(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercab_wireframe_vert,
        HOOD to R.drawable.supercab_hood_vert,
        TAILGATE to R.drawable.supercab_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.supercab_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.supercab_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.supercab_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.supercab_rrdoor_vert,
        BODY_PRIMARY to R.drawable.supercab_primary_vert,
        BODY_SECONDARY to R.drawable.supercab_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercab_wireframe_horz,
        HOOD to R.drawable.supercab_hood_horz,
        TAILGATE to R.drawable.supercab_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.supercab_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.supercab_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.supercab_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.supercab_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.supercab_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.supercab_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.filler,
        RIGHT_REAR_WINDOW to R.drawable.filler,
        BODY_PRIMARY to R.drawable.supercab_primary_horz,
        BODY_SECONDARY to R.drawable.supercab_secondary_horz,
    )

    override val name = "F-150 Super Cab"
}

open class F150SuperCrew(VIN: String) : F150(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercrew_wireframe_vert,
        HOOD to R.drawable.supercrew_hood_vert,
        TAILGATE to R.drawable.supercrew_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.supercrew_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.supercrew_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.supercrew_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.supercrew_rrdoor_vert,
        BODY_PRIMARY to R.drawable.supercrew_primary_vert,
        BODY_SECONDARY to R.drawable.supercrew_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercrew_wireframe_horz,
        HOOD to R.drawable.supercrew_hood_horz,
        TAILGATE to R.drawable.supercrew_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.supercrew_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.supercrew_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.supercrew_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.supercrew_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.supercrew_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.supercrew_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.supercrew_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.supercrew_rrwindow_horz,
        BODY_PRIMARY to R.drawable.supercrew_primary_horz,
        BODY_SECONDARY to R.drawable.supercrew_secondary_horz,
    )

    override val name = "F-150 Super Crew"
}

open class F250(VIN: String) : Vehicle(VIN) {
    override val layoutID = R.layout.f150_widget
    override val offsetPositions = arrayOf(400, 272)
    override val logoID = R.drawable.ford_f250_logo
}

class F250RegularCab(VIN: String) : F250(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.regularcab_wireframe_vert,
        HOOD to R.drawable.regularcab_hood_vert,
        TAILGATE to R.drawable.regularcab_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.regularcab_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.regularcab_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        BODY_PRIMARY to R.drawable.regularcab_primary_vert,
        BODY_SECONDARY to R.drawable.regularcab_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.regularcab_wireframe_horz,
        HOOD to R.drawable.regularcab_hood_horz,
        TAILGATE to R.drawable.regularcab_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.regularcab_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.regularcab_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        LEFT_FRONT_WINDOW to R.drawable.regularcab_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.regularcab_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.filler,
        RIGHT_REAR_WINDOW to R.drawable.filler,
        BODY_PRIMARY to R.drawable.regularcab_primary_horz,
        BODY_SECONDARY to R.drawable.regularcab_secondary_horz,
    )

    override val name = "F-250 Regular Cab"
}

class F250SuperCab(VIN: String) : F250(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercab_wireframe_vert,
        HOOD to R.drawable.supercab_hood_vert,
        TAILGATE to R.drawable.supercab_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.supercab_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.supercab_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.supercab_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.supercab_rrdoor_vert,
        BODY_PRIMARY to R.drawable.supercab_primary_vert,
        BODY_SECONDARY to R.drawable.supercab_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercab_wireframe_horz,
        HOOD to R.drawable.supercab_hood_horz,
        TAILGATE to R.drawable.supercab_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.supercab_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.supercab_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.supercab_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.supercab_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.supercab_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.supercab_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.filler,
        RIGHT_REAR_WINDOW to R.drawable.filler,
        BODY_PRIMARY to R.drawable.supercab_primary_horz,
        BODY_SECONDARY to R.drawable.supercab_secondary_horz,
    )

    override val name = "F-250 Super Cab"
}

class F250CrewCab(VIN: String) : F250(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercrew_wireframe_vert,
        HOOD to R.drawable.supercrew_hood_vert,
        TAILGATE to R.drawable.supercrew_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.supercrew_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.supercrew_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.supercrew_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.supercrew_rrdoor_vert,
        BODY_PRIMARY to R.drawable.supercrew_primary_vert,
        BODY_SECONDARY to R.drawable.supercrew_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.supercrew_wireframe_horz,
        HOOD to R.drawable.supercrew_hood_horz,
        TAILGATE to R.drawable.supercrew_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.supercrew_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.supercrew_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.supercrew_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.supercrew_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.supercrew_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.supercrew_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.supercrew_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.supercrew_rrwindow_horz,
        BODY_PRIMARY to R.drawable.supercrew_primary_horz,
        BODY_SECONDARY to R.drawable.supercrew_secondary_horz,
    )

    override val name = "F-250 Crew Cab"
}

open class RangerSuperCab(VIN: String) : F150SuperCab(VIN) {
    override val logoID = R.drawable.generic_logo
    override val offsetPositions = arrayOf(244, 200)
    override val name = "Ranger SuperCab"
}

open class RangerSuperCrew(VIN: String) : F150SuperCrew(VIN) {
    override val logoID = R.drawable.generic_logo
    override val offsetPositions = arrayOf(244, 200)
    override val name = "Ranger SuperCrew"
}

open class Maverick(VIN: String) : F150SuperCrew(VIN) {
    override val logoID = R.drawable.generic_logo
    override val offsetPositions = arrayOf(344, 800)
    override val name = "Maverick SuperCrew"
}

open class Bronco(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.bronco_base_4x4_wireframe_vert,
        HOOD to R.drawable.bronco_base_4x4_hood_vert,
        TAILGATE to R.drawable.bronco_base_4x4_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.bronco_base_4x4_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.bronco_base_4x4_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.bronco_base_4x4_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.bronco_base_4x4_rrdoor_vert,
        BODY_PRIMARY to R.drawable.bronco_base_4x4_primary_vert,
        BODY_SECONDARY to R.drawable.bronco_base_4x4_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.bronco_base_4x4_wireframe_horz,
        HOOD to R.drawable.bronco_base_4x4_hood_horz,
        TAILGATE to R.drawable.bronco_base_4x4_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.bronco_base_4x4_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.bronco_base_4x4_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.bronco_base_4x4_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.bronco_base_4x4_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.bronco_base_4x4_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.bronco_base_4x4_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.bronco_base_4x4_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.bronco_base_4x4_rrwindow_horz,
        BODY_PRIMARY to R.drawable.bronco_base_4x4_primary_horz,
        BODY_SECONDARY to R.drawable.bronco_base_4x4_secondary_horz,
    )

    override val layoutID = R.layout.bronco_widget
    override val offsetPositions = arrayOf(244, 200)
    override val logoID = R.drawable.bronco_logo
    override val name = "Bronco"
}

class Edge(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.edge_wireframe_vert,
        HOOD to R.drawable.edge_hood_vert,
        TAILGATE to R.drawable.edge_liftgate_vert,
        LEFT_FRONT_DOOR to R.drawable.edge_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.edge_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.edge_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.edge_rrdoor_vert,
        BODY_PRIMARY to R.drawable.edge_primary_vert,
        BODY_SECONDARY to R.drawable.edge_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.edge_wireframe_horz,
        HOOD to R.drawable.edge_hood_horz,
        TAILGATE to R.drawable.edge_liftgate_horz,
        LEFT_FRONT_DOOR to R.drawable.edge_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.edge_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.edge_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.edge_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.edge_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.edge_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.edge_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.edge_rrwindow_horz,
        BODY_PRIMARY to R.drawable.edge_primary_horz,
        BODY_SECONDARY to R.drawable.edge_secondary_horz,
    )

    override val layoutID = R.layout.edge_widget
    override val offsetPositions = arrayOf(240, 200)
    override val logoID = R.drawable.edge_logo
    override val name = "Edge"
}

open class Escape(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.escape_wireframe_vert,
        HOOD to R.drawable.escape_hood_vert,
        TAILGATE to R.drawable.escape_hatch_vert,
        LEFT_FRONT_DOOR to R.drawable.escape_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.escape_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.escape_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.escape_rrdoor_vert,
        BODY_PRIMARY to R.drawable.escape_primary_vert,
        BODY_SECONDARY to R.drawable.escape_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.escape_wireframe_horz,
        HOOD to R.drawable.escape_hood_horz,
        TAILGATE to R.drawable.escape_hatch_horz,
        LEFT_FRONT_DOOR to R.drawable.escape_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.escape_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.escape_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.escape_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.escape_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.escape_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.escape_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.escape_rrwindow_horz,
        BODY_PRIMARY to R.drawable.escape_primary_horz,
        BODY_SECONDARY to R.drawable.escape_secondary_horz,
    )

    override val layoutID = R.layout.escape_widget
    override val offsetPositions = arrayOf(340, 244)
    override val logoID = R.drawable.escape_logo
    override val name = "Escape"
}

class Mustang(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.mustang_wireframe_vert,
        HOOD to R.drawable.mustang_hood_vert,
        TAILGATE to R.drawable.mustang_hatch_vert,
        LEFT_FRONT_DOOR to R.drawable.mustang_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.mustang_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        BODY_PRIMARY to R.drawable.mustang_primary_vert,
        BODY_SECONDARY to R.drawable.mustang_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.mustang_wireframe_horz,
        HOOD to R.drawable.mustang_hood_horz,
        TAILGATE to R.drawable.mustang_hatch_horz,
        LEFT_FRONT_DOOR to R.drawable.mustang_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.mustang_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.filler,
        RIGHT_REAR_DOOR to R.drawable.filler,
        LEFT_FRONT_WINDOW to R.drawable.mustang_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.mustang_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.filler,
        RIGHT_REAR_WINDOW to R.drawable.filler,
        BODY_PRIMARY to R.drawable.mustang_primary_horz,
        BODY_SECONDARY to R.drawable.mustang_secondary_horz,
    )

    override val layoutID = R.layout.mustang_widget
    override val offsetPositions = arrayOf(164, 232)
    override val logoID = R.drawable.mustang_logo
    override val name = "Mustang"
}

open class Explorer(VIN: String) : Vehicle(VIN) {
    override val verticalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.explorer_wireframe_vert,
        HOOD to R.drawable.explorer_hood_vert,
        TAILGATE to R.drawable.explorer_tailgate_vert,
        LEFT_FRONT_DOOR to R.drawable.explorer_lfdoor_vert,
        RIGHT_FRONT_DOOR to R.drawable.explorer_rfdoor_vert,
        LEFT_REAR_DOOR to R.drawable.explorer_lrdoor_vert,
        RIGHT_REAR_DOOR to R.drawable.explorer_rrdoor_vert,
        BODY_PRIMARY to R.drawable.explorer_primary_vert,
        BODY_SECONDARY to R.drawable.explorer_secondary_vert,
    )

    override val horizontalDrawables: MutableMap<String, Int> = mutableMapOf(
        WIREFRAME to R.drawable.explorer_wireframe_horz,
        HOOD to R.drawable.explorer_hood_horz,
        TAILGATE to R.drawable.explorer_tailgate_horz,
        LEFT_FRONT_DOOR to R.drawable.explorer_lfdoor_horz,
        RIGHT_FRONT_DOOR to R.drawable.explorer_rfdoor_horz,
        LEFT_REAR_DOOR to R.drawable.explorer_lrdoor_horz,
        RIGHT_REAR_DOOR to R.drawable.explorer_rrdoor_horz,
        LEFT_FRONT_WINDOW to R.drawable.explorer_lfwindow_horz,
        RIGHT_FRONT_WINDOW to R.drawable.explorer_rfwindow_horz,
        LEFT_REAR_WINDOW to R.drawable.explorer_lrwindow_horz,
        RIGHT_REAR_WINDOW to R.drawable.explorer_rrwindow_horz,
        BODY_PRIMARY to R.drawable.explorer_primary_horz,
        BODY_SECONDARY to R.drawable.explorer_secondary_horz,
    )

    override val layoutID = R.layout.explorer_widget
    override val offsetPositions = arrayOf(320, 280)
    override val logoID = R.drawable.ford_explorer_logo
    override val name = "Explorer"
}

class Expedition(VIN: String) : Explorer(VIN) {
    override val offsetPositions = arrayOf(324, 304)
    override val logoID = R.drawable.expedition_logo
    override val name = "Expedition"
}

class BroncoSport(VIN: String) : Bronco(VIN) {
    override val name = "Bronco Sport"
}

class Kuga(VIN: String) : Escape(VIN) {
    override val offsetPositions = arrayOf(340, 280)
    override val name = "Kuga"
    override val logoID = R.drawable.generic_logo
}

class Puma(VIN: String) : Escape(VIN) {
    override val offsetPositions = arrayOf(172, 288)
    override val name = "Puma"
    override val logoID = R.drawable.generic_logo
}

class Focus(VIN: String) : Escape(VIN) {
    override val offsetPositions = arrayOf(316, 316)
    override val name = "Focus"
    override val logoID = R.drawable.focus_logo
}