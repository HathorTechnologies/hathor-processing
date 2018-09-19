package com.hathor.docs.controllers;

public final class Api {
    private Api() {}

    public static final String ROOT_PATH = "/docs";

    public static class BuildVersion {
        public static final String VERSION = "/version";
    }

    public static class TemporaryFile {
        public static final String TMP_FILE = "/tmp-file";
        public static final String TMP_FILE_BY_ID = TMP_FILE + "/{file_id}";
    }

    public static class Files {
        public static final String DATA = "/data";
        public static final String FILES = "/files";
        public static final String DATA_BY_ID = DATA + "/{data_id}";
        public static final String FILES_BY_DATA_ID = DATA_BY_ID + FILES;
        public static final String FILES_BY_ID = FILES_BY_DATA_ID + "/{file_id}";
    }
}
