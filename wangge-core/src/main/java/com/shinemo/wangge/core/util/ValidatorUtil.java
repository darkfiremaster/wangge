package com.shinemo.wangge.core.util;

import com.shinemo.common.tools.exception.ApiException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * 数据校验
 */
public class ValidatorUtil {
	private static Validator validator;

	static {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * 校验对象
	 *
	 * @param object 待校验对象
	 * @param groups 待校验的组
	 */
	public static void validateEntity(Object object, Class<?>... groups)
			throws ApiException {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
				throw new ApiException(constraintViolation.getMessage());
			}
		}
	}
	


	public static <T> void validateList(List<T> objectList, Class<?>... groups) throws ApiException {
		for (Object o : objectList) {
			validateEntity(o);
		}
	}

}