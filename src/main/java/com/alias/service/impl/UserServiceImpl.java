package com.alias.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alias.common.ErrorCode;
import com.alias.constant.CommonConstant;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.model.dto.user.UserQueryRequest;
import com.alias.model.dto.user.UserUpdateDTO;
import com.alias.model.enums.UserRoleEnum;
import com.alias.model.vo.UserVO;
import com.alias.utils.JwtUtils;
import com.alias.utils.SqlUtils;
import com.alias.utils.UsernameGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.User;
import com.alias.service.UserService;
import com.alias.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alias.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author alias
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-06-09 21:47:16
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    public static final String SALT = "alias";

    @Resource
    private UserMapper userMapper;

    @Override
    public UserVO userLogin(String username, String password, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        token = token.replace("Bearer ", "");
        Long userId = JwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (user.getDeleteTime() != null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已删除");
        }
        if (user.getUserRole().equals(UserRoleEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已被封禁");
        }

        return user;

//        // 先判断是否已登录
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null || currentUser.getId() == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//        // 从数据库查询（追求性能可以注释，直接走缓存）
//        long userId = currentUser.getId();
//        currentUser = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//        return currentUser;
    }

    @Override
    public UserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setOpenId(mpOpenId);
                user.setAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUsername(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getUserVO(user);
        }
    }

    @Override
    public UserVO userLoginByOpenId(String openId, String unionId, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("open_id", openId);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            user = new User();
            user.setUnionId(unionId);
            user.setOpenId(openId);
            user.setUserRole(UserRoleEnum.USER.getValue());
            // random username
            user.setUsername(UsernameGenerator.generate());

            String defaultPassword = "123456";
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
            user.setPassword(encryptPassword);

            boolean result = this.save(user);

            ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "用户创建失败");

            user = this.getById(user.getId());
        }

        String token = JwtUtils.generateToken(user);
        UserVO userVO = getUserVO(user);
        userVO.setToken(token);

        return userVO;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        return isAdmin(currentUser);
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);

        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO, long userId) {
        if (!userUpdateDTO.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "不能修改其他用户信息");
        }

        User existingUser = this.getById(userId);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        User updatedUser = new User();
        BeanUtil.copyProperties(userUpdateDTO, updatedUser);

        boolean updated = this.updateById(updatedUser);
        if (!updated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getOpenId();
        String username = userQueryRequest.getUsername();
        String userRole = userQueryRequest.getUserRole();
        String email = userQueryRequest.getEmail();
        String phoneNumber = userQueryRequest.getPhoneNumber();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "openId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phoneNumber), "phoneNumber", phoneNumber);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




