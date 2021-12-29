package com.skillbox.javapro21.api.response.account;

import com.skillbox.javapro21.api.response.Content;
import com.skillbox.javapro21.config.Constants;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AvatarUploadData implements Content {

    private static Integer id = 0;
    private Long ownerId;
    private String fileName;
    private String relativeFilePath;
    private String rawFileURL;
    private String fileFormat;
    private Long bytes;
    private static final Constants.FileType fileType = Constants.FileType.IMAGE;
    private Long createdAt;
}
