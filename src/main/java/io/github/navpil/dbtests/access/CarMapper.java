package io.github.navpil.dbtests.access;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//Used by mybatis
public interface CarMapper {


//    @Results(id = "carResult", value = {
//            @Result(property = "maxSpeed", column = "max_speed"),
//    })
//    @Select("SELECT * FROM car")
    List<Car> listAll();

}
