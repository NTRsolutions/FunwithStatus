package com.epsilon.FunwithStatus.adapter;

import android.graphics.Bitmap;

public class GridViewVideoItem {

        private String path;
        private boolean isDirectory;
        private String image;


        public GridViewVideoItem(String path, boolean isDirectory, String image) {
            this.path = path;
            this.isDirectory = isDirectory;
            this.image = image;
        }


        public String getPath() {
            return path;
        }


        public boolean isDirectory() {
            return isDirectory;
        }


        public String getImage() {
            return image;
        }
    }

