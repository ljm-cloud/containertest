package com.example.containertest.entity;

import javax.persistence.*;
import java.util.Date;

@Table(name = "t_config")
public class Config {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "conf_key")
	private String confKey;
	
	@Column(name = "conf_value")
	private String confValue;

	@Column(name = "conf_remark")
	private String confRemark;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getConfRemark() {
		return confRemark;
	}

	public void setConfRemark(String confRemark) {
		this.confRemark = confRemark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getConfKey() {
		return confKey;
	}

	public void setConfKey(String confKey) {
		this.confKey = confKey;
	}

	public String getConfValue() {
		return confValue;
	}

	public void setConfValue(String confValue) {
		this.confValue = confValue;
	}
	
}
