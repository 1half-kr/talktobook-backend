package com.lifelibrarians.lifebookshelf.image.service;

import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import com.lifelibrarians.lifebookshelf.image.domain.ObjectResourceManager;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.utils.exception.UtilsExceptionStatus;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class ImageService {

	@Value("${images.path.profile}")
	private String PROFILE_IMAGE_DIR;
	@Value("${images.path.bio-cover}")
	private String BIO_COVER_IMAGE_DIR;
	@Value("${images.path.book-cover}")
	private String BOOK_COVER_IMAGE_DIR;
	private final ObjectResourceManager objectResourceManager;
	private final List<String> validImageExtension = List.of(".jpg", ".jpeg", ".png");

	public String getPreSignedUrl(String imageUrl) {
		if (!isValidDirName(imageUrl)) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		}
		if (!isImageExtension(getExtension(imageUrl))) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		}
		return objectResourceManager.getPreSignedUrl(imageUrl);
	}

	private boolean isValidDirName(String imageUrl) {
		List<String> dirNameList = List.of(PROFILE_IMAGE_DIR, BIO_COVER_IMAGE_DIR,
				BOOK_COVER_IMAGE_DIR);
		String dirName = getDirName(imageUrl);
		if (Objects.isNull(dirName) || dirName.isBlank()) {
			return false;
		}
		return dirNameList.contains(dirName);
	}

	private String getDirName(String imageUrl) {
		if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
			return null;
		}
		return imageUrl.substring(0, imageUrl.indexOf("/") + 1);
	}

	public String getImageUrl(String imageUrl) {
		if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
			return null;
		}
		return objectResourceManager.getObjectUrl(imageUrl);
	}

	public String parseImageUrl(String imageUrl, String dirName) {
		if (!imageUrl.contains(dirName)) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_URL);
		}
		if (!isImageExtension(getExtension(imageUrl))) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		}
		String objectKey = imageUrl.substring(imageUrl.indexOf(dirName));
		if (!objectResourceManager.doesObjectExist(objectKey)) {
			return null;
		}
		return objectKey;
	}

	private boolean isInDirectory(String imageUrl, String dirName) {
		return imageUrl.startsWith(dirName);
	}

	/**
	 * 올바른 이미지 확장자인지 검사한다.
	 *
	 * @param extension 확장자
	 * @return 올바른 이미지 확장자인지 여부
	 */
	private boolean isImageExtension(String extension) {
		return validImageExtension.contains(extension.toLowerCase());
	}

	private String getExtension(String filename) {
		if (!filename.contains(".")) {
			throw new ServiceException(UtilsExceptionStatus.INVALID_FILE_EXTENSION);
		}
		return filename.substring(filename.lastIndexOf("."));
	}
}
