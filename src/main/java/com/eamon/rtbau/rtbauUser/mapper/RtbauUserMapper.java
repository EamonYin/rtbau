package com.eamon.rtbau.rtbauUser.mapper;

import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
@Service
public interface RtbauUserMapper extends BaseMapper<RtbauUser> {

    //获取数据库中所有用户涉及的地域编号list1
    List<String> getAllRegionCode();

    //获取数据库中恶劣天气地域的所有用户uid
    List<String> getSendUids(List<String> regionCodes);

}
