package com.example.khughes.machewidget

import android.content.Context
import android.graphics.*
import android.icu.text.MessageFormat
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.VINInfo.Companion.isMachE
import com.example.khughes.machewidget.db.UserInfoDatabase
import com.example.khughes.machewidget.db.VehicleInfoDatabase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets


class VINInfo {

    companion object {

        private const val WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1
        private const val WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3
        private const val WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV = "3FM"
        private const val WORLD_MANUFACTURING_IDENTIFIER_GERMANY = "WF0"
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
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV =
            "W1S" // 4x2, SuperCrew, SSV (Special Service Vehicle), government
        private const val NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV =
            "W1T" // 4x4, superCrew, SSV (Special Service Vehicle), government
        private const val NA_LINE_SERIES_F150_SUPERCAB_4X2 = "X1C" // 4x2, SuperCab
        private const val NA_LINE_SERIES_F150_SUPERCAB_4X4 = "X1E" // 4x4, SuperCab

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

        private const val EURO_LINE_SERIES_START_INDEX = 7 - 1
        private const val EURO_LINE_SERIES_END_INDEX = 9

        private const val EURO_LINE_SERIES_KUGA = "WPM"
        private const val EURO_LINE_SERIES_PUMA = "ERK"

        private val macheLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_MACHE_SELECT_RWD,
            NA_LINE_SERIES_MACHE_SELECT_RWD,
            NA_LINE_SERIES_MACHE_SELECT_AWD,
            NA_LINE_SERIES_MACHE_CAROUTE1_RWD,
            NA_LINE_SERIES_MACHE_PREMIUM_RWD,
            NA_LINE_SERIES_MACHE_PREMIUM_AWD,
            NA_LINE_SERIES_MACHE_GT_RWD,
        )

        @JvmStatic
        fun isMachE(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return (wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY || wmi == WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV)
                    && macheLineSeries.contains(lineSeries)
        }

        private val f150RegularCabsLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_F150_REGULAR_4X2,
            NA_LINE_SERIES_F150_REGULAR_4X4,
        )

        @JvmStatic
        fun isF150RegularCab(VIN: String): Boolean {
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return f150RegularCabsLineSeries.contains(lineSeries)
        }

        private val f150SuperCrewsLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_F150_SUPERCREW_4X2,
            NA_LINE_SERIES_F150_SUPERCREW_4X4,
            NA_LINE_SERIES_F150_SUPERCREW_4X4_POLICE,
            NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV,
            NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV,
        )

        @JvmStatic
        fun isF150SuperCrew(VIN: String): Boolean {
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return f150SuperCrewsLineSeries.contains(lineSeries)
        }

        @JvmStatic
        fun isF150Raptor(VIN: String): Boolean {
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return lineSeries == NA_LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR
        }

        private val f150SuperCabsLineSeries: Set<String> = setOf(
            NA_LINE_SERIES_F150_SUPERCAB_4X2,
            NA_LINE_SERIES_F150_SUPERCAB_4X4,
        )

        @JvmStatic
        fun isF150SuperCab(VIN: String): Boolean {
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return f150SuperCabsLineSeries.contains(lineSeries)
        }

        @JvmStatic
        fun isF150(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK &&
                    (isF150RegularCab(VIN) || isF150SuperCab(VIN) || isF150SuperCrew(VIN) || isF150Raptor(
                        VIN
                    ))
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

        @JvmStatic
        fun isExplorer(VIN: String): Boolean {
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

        @JvmStatic
        fun isBronco(VIN: String): Boolean {
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

        @JvmStatic
        fun isBroncoSport(VIN: String): Boolean {
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

        @JvmStatic
        fun isEscape(VIN: String): Boolean {
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

        @JvmStatic
        fun isEdge(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && edgeLineSeries.contains(lineSeries)
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

        @JvmStatic
        fun isExpedition(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_USA_MPV
                    && expeditionLineSeries.contains(lineSeries)
        }

        @JvmStatic
        fun isKuga(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY && lineSeries == EURO_LINE_SERIES_KUGA
        }

        @JvmStatic
        fun isPuma(VIN: String): Boolean {
            val wmi = VIN.substring(
                WORLD_MANUFACTURING_IDENTIFIER_START_INDEX,
                WORLD_MANUFACTURING_IDENTIFIER_END_INDEX
            )
            val lineSeries =
                VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX)
            return wmi == WORLD_MANUFACTURING_IDENTIFIER_GERMANY && lineSeries == EURO_LINE_SERIES_PUMA
        }

        // Check to see if we recognize a VIN in general
        @JvmStatic
        fun isVINRecognized(VIN: String): Boolean =
            isMachE(VIN) || isF150(VIN) || isBronco(VIN) || isExplorer(VIN) || isBroncoSport(VIN)
                    || isEscape(VIN) || isEdge(VIN) || isExpedition(VIN) || isKuga(VIN)
                    || isPuma(VIN)

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

    }
}

class VehicleDrawables {

    companion object {
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

        // Drawables for Mach-E
        private val macheDrawables: Map<String, Int> = mapOf(
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

        private val macheDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for Regular Cab (two door) F-150
        private val regcabDrawables: Map<String, Int> = mapOf(
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

        private val regcabDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for SuperCab F-150
        private val supercabDrawables: Map<String, Int> = mapOf(
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

        private val supercabDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for SuperCrew F-150
        private val supercrewDrawables: Map<String, Int> = mapOf(
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

        private val supercrewDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for Bronco Base 4x4
        private val broncobase4x4Drawables: Map<String, Int> = mapOf(
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

        private val broncobase4x4Drawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for Explorer ST
        private val explorerSTDrawables: Map<String, Int> = mapOf(
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

        private val explorerSTDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for Escape
        private val escapeDrawables: Map<String, Int> = mapOf(
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

        private val escapeDrawables_1x5: Map<String, Int> = mapOf(
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

        // Drawables for Edge
        private val edgeDrawables: Map<String, Int> = mapOf(
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

        private val edgeDrawables_1x5: Map<String, Int> = mapOf(
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

        // Get the set of drawables for a particular style of vehicle
        @JvmStatic
        fun getVerticalVehicleDrawables(VIN: String?): Map<String, Int> {
            VIN?.let {
                if (VIN != "") {
                    if (VINInfo.isF150(VIN)) {
                        if (VINInfo.isF150RegularCab(VIN)) {
                            return regcabDrawables
                        } else if (VINInfo.isF150SuperCab(VIN)) {
                            return supercabDrawables
                        } else if (VINInfo.isF150SuperCrew(VIN) || VINInfo.isF150Raptor(VIN)) {
                            return supercrewDrawables
                        }
                    } else if (VINInfo.isBronco(VIN) || VINInfo.isBroncoSport(VIN)) {
                        return broncobase4x4Drawables
                    } else if (VINInfo.isExplorer(VIN)) {
                        return explorerSTDrawables
                    } else if (VINInfo.isEscape(VIN) || VINInfo.isKuga(VIN) || VINInfo.isPuma(VIN)) {
                        return escapeDrawables
                    } else if (VINInfo.isEdge(VIN)) {
                        return edgeDrawables
                    } else if (VINInfo.isExpedition(VIN)) {
                        return explorerSTDrawables
                    }
                }
            }
            return macheDrawables
        }

        @JvmStatic
        fun getHorizontalVehicleDrawable(VIN: String?): Map<String, Int> {
            VIN?.let {
                if (VIN != "") {
                    if (VINInfo.isF150(VIN)) {
                        if (VINInfo.isF150RegularCab(VIN)) {
                            return regcabDrawables_1x5
                        } else if (VINInfo.isF150SuperCab(VIN)) {
                            return supercabDrawables_1x5
                        } else if (VINInfo.isF150SuperCrew(VIN) || VINInfo.isF150Raptor(VIN)) {
                            return supercrewDrawables_1x5
                        }
                    } else if (VINInfo.isBronco(VIN) || VINInfo.isBroncoSport(VIN)) {
                        return broncobase4x4Drawables_1x5
                    } else if (VINInfo.isExplorer(VIN)) {
                        return explorerSTDrawables_1x5
                    } else if (VINInfo.isEscape(VIN) || VINInfo.isKuga(VIN) || VINInfo.isPuma(VIN)) {
                        return escapeDrawables_1x5
                    } else if (VINInfo.isEdge(VIN)) {
                        return edgeDrawables_1x5
                    } else if (VINInfo.isExpedition(VIN)) {
                        return explorerSTDrawables_1x5
                    }
                }
            }
            return macheDrawables_1x5
        }

        @JvmStatic
        fun getLayoutByVIN(VIN: String?): Int {
            VIN?.let {
                if (VIN != "") {
                    if (VINInfo.isF150(VIN)) {
                        return R.layout.f150_widget
                    } else if (VINInfo.isBronco(VIN) || VINInfo.isBroncoSport(VIN)) {
                        return R.layout.bronco_widget
                    } else if (VINInfo.isExplorer(VIN)) {
                        return R.layout.explorer_widget
                    } else if (VINInfo.isEscape(VIN) || VINInfo.isKuga(VIN) || VINInfo.isPuma(VIN)) {
                        return R.layout.escape_widget
                    } else if (VINInfo.isEdge(VIN)) {
                        return R.layout.edge_widget
                    } else if (VINInfo.isExpedition(VIN)) {
                        return R.layout.explorer_widget
                    }
                }
            }
            return R.layout.mache_widget
        }
    }
}

class VehicleColor {
    companion object {
        const val ARGB_MASK: Int = 0xffffff  // only use RGB components
        const val WIREFRAME_MASK = 0x03 shl 24
        const val WIREFRAME_WHITE = 0
        const val WIREFRAME_BLACK = 1 shl 24
        const val WIREFRAME_AUTO = 2 shl 24

        // Attempt to automatically choose the color of the vehicle for the widget
        @JvmStatic
        fun scanImageForColor(context: Context, vehicleInfo: VehicleInfo): Boolean {
            // If vehicle color has been set, do nothing
            if ((vehicleInfo.colorValue and ARGB_MASK) != (Color.WHITE and ARGB_MASK)) {
                return false
            }

            // If the vehicle image doesn't exist, do nothing
            val VIN = vehicleInfo.vin
            val bmp = Utils.getVehicleImage(context, VIN, 4)
            if (bmp == null || vehicleInfo.colorValue != Color.WHITE) {
                return false
            }

            // Based on the vehicle type, choose a small image patch to sample
            val (startx, starty) =
                if (isMachE(VIN)) listOf(352, 288)
                else if (VINInfo.isF150(VIN)) listOf(344, 220)
                else if (VINInfo.isBronco(VIN) || VINInfo.isBroncoSport(VIN)) listOf(244, 200)
                else if (VINInfo.isExplorer(VIN)) listOf(320, 280)
                else if (VINInfo.isEscape(VIN)) listOf(340, 244)
                else if (VINInfo.isKuga(VIN)) listOf(340, 280)
                else if (VINInfo.isPuma(VIN)) listOf(172, 288)
                else if (VINInfo.isEdge(VIN)) listOf(240, 200)
                else if (VINInfo.isExpedition(VIN)) listOf(324, 304)
                else listOf(0, 0)

            // If the VIN is not recognized, this is unsupported
            if (startx == 0) {
                return false
            }

            // get the RBG value of each pixel in the patch
            val RGB = IntArray(3)
            val patchSize = 10
            for (y in 0..patchSize) {
                for (x in 0..patchSize) {
                    val color = bmp.getPixel(startx + x, starty + y)
                    RGB[0] += color.shr(16) and 0xff
                    RGB[1] += color.shr(8) and 0xff
                    RGB[2] += color and 0xff
                }
            }

            // average the components
            RGB[0] /= patchSize * patchSize
            RGB[1] /= patchSize * patchSize
            RGB[2] /= patchSize * patchSize

            // Set the color and exit
            vehicleInfo.colorValue = (((RGB[0] shl 16) or (RGB[1] shl 8) or RGB[2])
                    and ARGB_MASK) or WIREFRAME_AUTO
            return true
        }

        @JvmStatic
        fun isFirstEdition(context: Context, VIN: String): Boolean {
            // If the vehicle isn't a Mach-E, nevermind
            if (!isMachE(VIN)) {
                return false
            }

            // If the vehicle image doesn't exist, do nothing
            val bmp = Utils.getVehicleImage(context, VIN, 4) ?: return false

            // Check if a pixel on the side view mirror is black or colored
            val color = bmp.getPixel(220, 152)
            val RGB = arrayOf(
                (color shr 16) and 0xff,
                (color shr 8) and 0xff,
                color and 0xff
            )

            return RGB[0] > 0x08 || RGB[1] > 0x08 || RGB[2] > 0x08
        }

        @JvmStatic
        fun drawColoredVehicle(
            context: Context, bmp: Bitmap, color: Int, whatsOpen: MutableList<Int>,
            useColor: Boolean, vehicleImages: Map<String, Int>
        ) {
            // Create base canvas the size of the image
            val canvas = Canvas(bmp)
            val paint = Paint()
            var bmp2: Bitmap
            var canvas2: Canvas

            val drawableId = vehicleImages[VehicleDrawables.BODY_PRIMARY]
            if (drawableId != null && useColor) {
                val drawable = AppCompatResources.getDrawable(context, drawableId)
                drawable?.let {
                    bmp2 = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                    )
                    canvas2 = Canvas(bmp2)

                    // Fill with the primary color mask
                    paint.color = color and ARGB_MASK
                    // Set the alpha based on whether something is open
                    paint.alpha = if (whatsOpen.isEmpty()) 0xff else 0xbf
                    paint.style = Paint.Style.FILL
                    canvas.drawPaint(paint)

                    // Draw the primary body in color
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas2)
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                    canvas.drawBitmap(bmp2, 0f, 0f, paint)
                }

                // If secondary colors exist, add them
                val secondary = vehicleImages[VehicleDrawables.BODY_SECONDARY]
                secondary?.let {
                    val icon = AppCompatResources.getDrawable(context, it)
                    icon?.let {
                        icon.setBounds(0, 0, canvas.width, canvas.height)
                        icon.draw(canvas)
                    }
                }
            }

            // Draw anything that's open
            for (id in whatsOpen) {
                val icon = context.getDrawable(id)
                icon?.let {
                    icon.setBounds(0, 0, canvas.width, canvas.height)
                    icon.draw(canvas)
                }
            }

            // Create a second bitmap the same size as the primary
            bmp2 = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
            canvas2 = Canvas(bmp2)

            // If not using colors, draw wireframe in white
            if (!useColor) {
                paint.color = Color.WHITE
            }
            // Figure out whether wireframe should be drawn light or dark
            else {
                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(color and ARGB_MASK, hsl)
                val wireframeMode = color and WIREFRAME_MASK
                paint.color =
                    if (wireframeMode == WIREFRAME_WHITE) Color.WHITE
                    else if (wireframeMode == WIREFRAME_BLACK) Color.BLACK
                    else if (hsl[2] > 0.5) Color.BLACK
                    else Color.WHITE
            }
            paint.alpha = 0xff
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

            // Fill with a contrasting color
            paint.style = Paint.Style.FILL
            canvas2.drawPaint(paint)

            // Draw the wireframe body
            val drawable = AppCompatResources.getDrawable(
                context,
                vehicleImages[VehicleDrawables.WIREFRAME]!!
            )
            drawable?.let {
                val bmp3 = Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas3 = Canvas(bmp3)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas3)

                // Set the wireframe's color
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                canvas2.drawBitmap(bmp3, 0f, 0f, paint)
            }

            // Draw wireframe over the colored body
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            canvas.drawBitmap(bmp2, 0f, 0f, paint)
        }
    }
}

class PrefManagement {

    private lateinit var jsonOutput: String

    fun savePrefs(context: Context) {
        GlobalScope.launch {
            jsonOutput = getInfo(context)
            val inStream: InputStream = ByteArrayInputStream(
                jsonOutput.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
            val outputFilename = Utils.writeExternalFile(
                context,
                inStream,
                "fsw_settings-",
                Constants.APPLICATION_JSON
            )
            Toast.makeText(
                context,
                MessageFormat.format(
                    "Settings file \"{0}.json\" copied to Download folder.",
                    outputFilename
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val JSON_SETTINGS_VERSION = 2
    }

    private suspend fun getInfo(context: Context): String =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val jsonData = LinkedHashMap<String, Any>()
                jsonData.put("version", JSON_SETTINGS_VERSION)

                // Save the default preferences
                var prefs = PreferenceManager.getDefaultSharedPreferences(context).all
                val prefData = LinkedHashMap<String, Array<String>>()
                for(key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        else -> "Boolean"
                    }
                    prefData.put(key, arrayOf( dataType, value.toString()))
                }
                jsonData.put("prefs", prefData.clone())
                prefData.clear()

                // Save the shared preferences
                prefs = context.getSharedPreferences(StoredData.TAG, Context.MODE_PRIVATE).all
                for(key in prefs.keys) {
                    val value = prefs[key]
                    val dataType = when (value) {
                        is String -> "String"
                        is Long -> "Long"
                        is Int -> "Integer"
                        else -> "Boolean"
                    }
                    prefData.put(key, arrayOf( dataType, value.toString()))
                }
                jsonData.put(StoredData.TAG, prefData.clone())
                prefData.clear()

                // Save database entries
                jsonData.put("users", UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo())
                jsonData.put("vehicles", VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo())
                GsonBuilder().create().toJson(jsonData)
           }
        }

}
