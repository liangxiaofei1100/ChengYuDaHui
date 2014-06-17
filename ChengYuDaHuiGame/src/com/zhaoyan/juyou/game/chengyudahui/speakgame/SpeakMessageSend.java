package com.zhaoyan.juyou.game.chengyudahui.speakgame;

import com.google.protobuf.InvalidProtocolBufferException;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.Command;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.GameType;
import com.zhaoyan.juyou.game.chengyudahui.protocol.pb.SpeakGameProtos.SpeakGameMsg.RoleType;

public class SpeakMessageSend {
	private static SpeakMessageSend mSpeakMessageSend;

	private SpeakMessageSend() {

	}

	public static SpeakMessageSend getInstance() {
		if (mSpeakMessageSend == null)
			mSpeakMessageSend = new SpeakMessageSend();
		return mSpeakMessageSend;
	}

	public SpeakGameMsg getSendMessage(GameType gameType, Command cmd,
			int word, int time, RoleType type) {
		SpeakGameMsg.Builder builder = SpeakGameMsg.newBuilder();
		builder.setGame(gameType);
		builder.setCommand(cmd);
		if (word > 0) {
			builder.setWord(word);
		}
		if (time > -1) {
			builder.setTime(time);
		}
		builder.setType(type);
		return builder.build();
	}

	public SpeakGameMsg parseMsg(byte[] data) {
		SpeakGameMsg message = null;
		try {
			message = SpeakGameMsg.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
}
