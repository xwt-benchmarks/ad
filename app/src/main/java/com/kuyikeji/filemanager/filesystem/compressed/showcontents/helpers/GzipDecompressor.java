/*
 * GzipDecompressor.java
 *
 * Copyright (C) 2017-2018 Emmanuel Messulam<emmanuelbendavid@gmail.com>,
 * Raymond Lai <airwave209gt@gmail.com>.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kuyikeji.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.kuyikeji.filemanager.adapters.data.CompressedObjectParcelable;
import com.kuyikeji.filemanager.asynchronous.asynctasks.compress.CompressedHelperTask;
import com.kuyikeji.filemanager.asynchronous.asynctasks.compress.GzipHelperTask;
import com.kuyikeji.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.kuyikeji.filemanager.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class GzipDecompressor extends Decompressor {

    public GzipDecompressor(Context context) {
        super(context);
    }

    @Override
    public CompressedHelperTask changePath(String path, boolean addGoBackItem,
                                           OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new GzipHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
