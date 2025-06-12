package com.alias.service;

import com.alias.model.dto.user.UserQueryRequest;
import com.alias.model.dto.user.UserUpdateDTO;
import com.alias.model.entity.User;
import com.alias.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author alias
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-06-09 21:47:16
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param username  用户账户
     * @param password 用户密码
     * @param request HttpServletRequest对象
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String username, String password, HttpServletRequest request);

    /**
     * 获取登录用户信息
     * @param request HttpServletRequest对象
     * @return 登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 用户登录或注册（小程序）
     * @param openId
     * @param unionId
     * @param request
     * @return
     */
    UserVO userLoginByOpenId(String openId, String unionId, HttpServletRequest request);

    /**
     * 检查用户是否为管理员
     * @param user 用户对象
     * @return true 如果用户是管理员，否则 false
     */
    boolean isAdmin(User user);

    /**
     * 检查请求中的用户是否为管理员
     * @param request HttpServletRequest对象
     * @return true 如果请求中的用户是管理员，否则 false
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 获取 UserVO 对象
     * @param user User 对象
     * @return UserVO 对象
     */
    UserVO getUserVO(User user);

    /**
     * 获取 UserVO 列表
     * @param userList User 对象列表
     * @return UserVO 列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 更新用户信息
     * @param userUpdateDTO 用户对象
     */
    void updateUserInfo(UserUpdateDTO userUpdateDTO, long userId);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
