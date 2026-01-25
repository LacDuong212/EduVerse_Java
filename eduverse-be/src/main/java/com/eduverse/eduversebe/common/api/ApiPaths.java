package com.eduverse.eduversebe.common.api;

public final class ApiPaths {
    private ApiPaths() {}

    public static final String API = "/api";

    public static final class Courses {
        private Courses() {}
        public static final String ROOT = API + "/courses";
    }

    public static final class Instructor {
        private Instructor() {}
        public static final String ROOT = API + "/instructor";

        public static final String CHART = ROOT + "/charts";
        public static final String MY_COURSES = ROOT + "/courses";
        public static final String STATS = ROOT + "/stats";
    }

    public static final class Instructors {
        private Instructors() {}
        public static final String ROOT = API + "/instructors";
    }
}

