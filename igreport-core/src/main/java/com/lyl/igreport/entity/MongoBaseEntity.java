package com.lyl.igreport.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by liuyanling on 2020/2/1
 */
@Getter
@Setter
public class MongoBaseEntity implements Serializable {
    private static final long serialVersionUID = 8962436951603976954L;

    private String id;

    private Date dateCreated;

    private String createdBy;

    private Date dateUpdated;

    private String updatedBy;

}
