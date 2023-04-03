package com.fastcampus.ch4.verification;

import com.fastcampus.ch4.dao.UserDao;
import com.fastcampus.ch4.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

    @Autowired
    UserDao userDao;

    @Override
    public boolean supports(Class<?> clazz) {
//		return User.class.equals(clazz); // 검증하려는 객체가 User타입인지 확인
        return User.class.isAssignableFrom(clazz); // clazz가 User 또는 그 자손인지 확인
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("UserValidator.validate() is called");

        User user = (User)target;

        String id = user.getId();
        String pwd = user.getPwd();

//		if(id==null || "".equals(id.trim())) {
//			errors.rejectValue("id", "required");
//		}
//        if(pwd==null || "".equals(pwd.trim())) {
//            errors.rejectValue("pwd", "required");
//        }

        //에러 메시지 가져오기
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id",  "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pwd", "required");

        if(id==null || id.length() <  5 || id.length() > 12) {
            errors.rejectValue("id", "invalidLength", new String[]{"5","12"}, null);
        }
        if(pwd==null || pwd.length() <  5 || id.length() > 12) {
            errors.rejectValue("pwd", "invalidLength", new String[]{"5","12"}, null);
        }

    }
}