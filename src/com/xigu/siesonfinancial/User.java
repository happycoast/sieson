package com.xigu.siesonfinancial;

import java.io.Serializable;
import android.graphics.Bitmap;

/**
 * 实体类，保存用户信息
 * 
 * @author iFlytek &nbsp;&nbsp;&nbsp;<a href="http://www.xfyun.cn/">讯飞语音云</a>
 * */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	// 用户名
	private String username;
	// 脸部照片
	private transient Bitmap facePic;
	// 是否已注册//
	private boolean isEnrolled;
	// 是否已登录
	private boolean isLogined;
	// 声纹标准分
	private double voice_score;
	// 人脸标准分
	private double face_score;
	// 融合标准分
	private double fusion_score;
	
	public User() {
		
	}
	
	public User(String username, Bitmap facePic) {
		this.username = username;
		this.facePic = facePic;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Bitmap getFacePic() {
		return facePic;
	}

	public void setFacePic(Bitmap facePic) {
		if (null != this.facePic) {
			this.facePic.recycle();
		}
		this.facePic = Bitmap.createBitmap(facePic);
	}
	
	public void setEnrolled(boolean enrolled) {
		this.isEnrolled = enrolled;
	}
	
	public boolean isEnrolled() {
		return this.isEnrolled;
	}
	
	public boolean isLogined() {
		return this.isLogined;
	}
	
	public void setLogined(boolean logined) {
		this.isLogined = logined;
	}

	public double getVoiceScore() {
		return voice_score;
	}

	public void setVoiceScore(double voice_score) {
		this.voice_score = voice_score;
	}

	public double getFaceScore() {
		return face_score;
	}

	public void setFaceScore(double face_score) {
		this.face_score = face_score;
	}

	public double getFusionScore() {
		return fusion_score;
	}

	public void setFusionScore(double fusion_score) {
		this.fusion_score = fusion_score;
	}
	
}
