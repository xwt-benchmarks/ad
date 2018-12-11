package com.kuyikeji.filemanager.filesystem.compressed.extractcontents;

import com.kuyikeji.filemanager.filesystem.compressed.extractcontents.helpers.RarExtractor;

public class RarExtractorTest extends AbstractExtractorTest {
    @Override
    protected String getArchiveType() {
        return "rar";
    }

    @Override
    protected Class<? extends Extractor> extractorClass() {
        return RarExtractor.class;
    }
}
