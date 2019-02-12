package com.example.android.simplealarmmanagerapp.utilities.constants

import android.graphics.Color

const val APPLICATION_NAME = "Attendance checking app"
const val TARGET_BEACON_ADDRESS_PREFERENCE_CONST = "targetBeaconAddress"
const val TARGET_BEACON_ADDRESS_CONST = "targetBeaconAddress"
const val MY_SECTION_URL = "https://attendance-app-dev.herokuapp.com/api/v1/me/sections"
const val SECTIONS_URL = "https://attendance-app-dev.herokuapp.com/api/v1/sections"
const val CLASSES_URL = "https://attendance-app-dev.herokuapp.com/api/v1/classes"
const val SIGN_IN_URL = "https://attendance-app-dev.herokuapp.com/api/v1/auth/signin"
const val STUDENT_URL = "https://attendance-app-dev.herokuapp.com/api/v1/students"

const val ATTENDANCE_URL = "https://attendance-app-dev.herokuapp.com/api/v1/attendances"

const val SECTION_ID_EXTRA = "sectionIdExtra"

const val TIME_TO_CLASS_ID = "timeToClassId"

const val SECTION_COURSE_TITLE = "sectionCourseTitle"

const val STARTING_CLASS_MESSAGE = "is going to start soon…"

const val JWT_HEADER_NAME = "X-Auth"

const val SIGN_UP_RESULT_HEADER_NAME = "message"

const val PROGRESS_DIALOG_SIGN_IN_MESSAGE = "Signing in..."

const val PROGRESS_DIALOG_SIGN_UP_MESSAGE = "Signing up..."

const val TOAST_NO_INTERNET_MESSAGE = "No internet connectivity =("

const val AUTH_PREFERENCE_NAME = "AuthenticationPreferences"

const val TARGET_DEVICE_PREFERENCES_NAME = "TargetDevicePreferences"

const val BAC_PREFERENCES_NAME = "AttendanceCheckScanPreferences"

const val WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX = "weeklyAttendanceCheckLoader"
const val CLASS_NOTIFICATION_PREFIX = "classNotificationPrefix"
const val WIFI_DISABLING_SERVICE_PREFIX = "wifiDisablingServicePrefix"
const val ATTENDANCE_CHECK_ALARM_PREFIX = "attendanceCheckAlarmPrefix"
const val ATTENDANCE_CHECK_SETUP_END_MESSAGE = "Scheduled attendance checks for the next week"
const val ATTENDANCE_CHECK_SETUP_START_MESSAGE =  "Loading attendance checks for the next week"

var PRIMARY_COLOR = Color.parseColor("#AB6C35")
var PRIMARY_DARK_COLOR = Color.parseColor("#935116")