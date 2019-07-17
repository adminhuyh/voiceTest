/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.cloud.asr.realtime.sdk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tencent.cloud.asr.realtime.sdk.asyn_sender.ReceiverEntrance;
import com.tencent.cloud.asr.realtime.sdk.cache_handler.FlowHandler;
import com.tencent.cloud.asr.realtime.sdk.config.AsrInternalConfig;
import com.tencent.cloud.asr.realtime.sdk.config.AsrPersonalConfig;
import com.tencent.cloud.asr.realtime.sdk.model.enums.EngineModelType;
import com.tencent.cloud.asr.realtime.sdk.model.enums.ResponseEncode;
import com.tencent.cloud.asr.realtime.sdk.model.enums.SdkRole;
import com.tencent.cloud.asr.realtime.sdk.model.enums.VoiceFormat;
import com.tencent.cloud.asr.realtime.sdk.model.response.TimeStat;
import com.tencent.cloud.asr.realtime.sdk.model.response.VoiceResponse;
import com.tencent.cloud.asr.realtime.sdk.utils.ByteUtils;
import com.tencent.cloud.asr.realtime.sdk.config.AsrGlobelConfig;
//import com.tencent.cloud.asr.realtime.sdk.model.response.VadResponse;
//import com.tencent.cloud.asr.realtime.sdk.model.response.VadResult;
//import com.tencent.cloud.asr.realtime.sdk.config.AsrBaseConfig;
//import com.tencent.cloud.asr.realtime.sdk.utils.JacksonUtil;

/**
 * 寮傛璋冪敤瀹炰緥銆備娇鐢ㄦ楠や负锛�
 * 
 * <pre>
 *  1. 閰嶇疆鍩烘湰鍙傛暟銆傚彧闇�鎵ц涓�娆°�� 
 *  2. 鏂板缓涓�涓紙鎴栧涓級鏈嶅姟 銆�
 *  3. 浣跨敤鏈嶅姟銆傚寘鎷細鍒濆鍖栨湇鍔� 銆佹敞鍐屽洖璋僅andler銆佸彂閫佹暟鎹紙鐢ㄦ埛绾跨▼A锛� 銆佹帴鏀舵暟鎹紙鐢ㄦ埛绾跨▼B锛�
 *  4. 鏈嶅姟鏈韩鍙互鎸佺画杩愯锛屼笉寤鸿棰戠箒鍒涘缓鍜岄攢姣佹湇鍔°��
 * </pre>
 * 
 * 璇﹁鏈琕oiceTask绫讳腑鐨剅un()鏂规硶銆�
 * 
 * @author iantang
 * @version 1.0
 */
public class RasrAsynRequestSample {

	static {
		initBaseParameters();
	}

	private int threadNumber = 1;
	private String voiceFile =  "C:/Users/zslt_hyh/Downloads/java_realtime_asr_sdk_v1.1/java_realtime_asr_sdk/test_wav/8k/8k.wav";

	private List<VoiceTask> taskList = new ArrayList<VoiceTask>();

	public static void main(String[] args) {
		RasrAsynRequestSample rasrRequestSample = new RasrAsynRequestSample();
		rasrRequestSample.setArguments(args);
		rasrRequestSample.start();

		sleepSomeTime(600000); // 10鍒嗛挓鍚庡仠姝㈢ず渚嬬▼搴忋��
		rasrRequestSample.stop();
		System.exit(0);
	}

	/**
	 * 鏍规嵁闇�姹傚惎鍔ㄥ涓换鍔★紝姣忎釜浠诲姟閮界嫭绔嬭繍琛岋紝浜掍笉骞叉壈銆�
	 */
	public void start() {
		for (int i = 1; i <= this.threadNumber; i++) {
			VoiceTask voiceTask = new VoiceTask(i, this.voiceFile);
			this.taskList.add(voiceTask);
			voiceTask.start();
			sleepSomeTime(20);
		}
	}

	/**
	 * 鍋滄鍏ㄩ儴浠诲姟銆�
	 */
	public void stop() {
		for (VoiceTask voiceTask : this.taskList) {
			voiceTask.stop();
		}
	}

	/**
	 * 鍒濆鍖栧熀纭�鍙傛暟, 璇峰皢涓嬮潰鐨勫弬鏁板�奸厤缃垚浣犺嚜宸辩殑鍊笺��
	 * 
	 * 鍙傛暟鑾峰彇鏂规硶鍙弬鑰冿細 <a href="https://cloud.tencent.com/document/product/441/6203">绛惧悕閴存潈 鑾峰彇绛惧悕鎵�闇�淇℃伅</a>
	 */
	private static void initBaseParameters() {
		// Required
		// AsrBaseConfig.appId = "YOUR_APP_ID_SET_HERE";
		// AsrBaseConfig.secretId = "YOUR_SECRET_ID";
		// AsrBaseConfig.secretKey = "YOUR_SECRET_KEY";

		// optional锛屾牴鎹嚜韬渶姹傞厤缃��
		AsrInternalConfig.setSdkRole(SdkRole.ONLINE); // VAD鐗堢敤鎴疯鍔″繀璧嬪�间负 SdkRole.VAD
		AsrPersonalConfig.responseEncode = ResponseEncode.UTF_8;
		AsrPersonalConfig.engineModelType = EngineModelType._8k_0;
		AsrPersonalConfig.voiceFormat = VoiceFormat.wav;

		// optional, 鍙拷鐣�
		AsrGlobelConfig.CUT_LENGTH = 4096; // 姣忔鍙戝線鏈嶅姟绔殑璇煶鍒嗙墖鐨勫瓧鑺傞暱搴︼紝8K璇煶寤鸿璁句负4096,16K璇煶寤鸿璁句负8192銆�
		// AsrGlobelConfig.NEED_VAD = 0; // 鏄惁瑕佸仛VAD锛岄粯璁や负1锛岃〃绀鸿鍋氥�傜嚎涓婄敤鎴蜂笉閫傜敤锛岃蹇界暐銆�
		// AsrGlobelConfig.NOTIFY_ALL_RESPONSE = true; // 鏄惁鍥炶皟姣忎釜鍒嗙墖鐨勫洖澶嶃�傚鍙渶鏈�鍚庣殑缁撴灉锛屽彲璁句负false銆�
		// AsrBaseConfig.PRINT_CUT_REQUEST = true; // 鎵撳嵃姣忎釜鍒嗙墖鐨勮姹傦紝鐢ㄤ簬娴嬭瘯銆�
		// AsrBaseConfig.PRINT_CUT_RESPONSE = true; // 鎵撳嵃涓棿缁撴灉锛岀敤浜庢祴璇曪紝鐢熶骇鐜寤鸿璁句负false銆�
		// 榛樿浣跨敤鑷畾涔夎繛鎺ユ睜锛岃繛鎺ユ暟鍙湪AsrGlobelConfig涓慨鏀癸紝鏇村缁嗚妭鍙傛暟锛屽彲鐩存帴淇敼婧愮爜HttpPoolingManager.java,鐒跺悗鑷鎵揓ar鍖呫��
		// AsrGlobelConfig.USE_CUSTOM_CONNECTION_POOL = true;
	}

	private static void sleepSomeTime(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * 鐢熸垚鍙墽琛孞ar鍚庯紝杩愯Jar鏃朵紶鍏ュ弬鏁扮殑绠�鍗曞鐞嗐�備笉寤鸿杩欐牱瀛愪紶鍙傛暟銆�
	 * 
	 * 姣斿杩欎釜鍛戒护浼犲叆浜�4涓弬鏁帮細 java -jar realAsrSdk_run.jar 10 test_wav/8k/8k_19s.wav 8k false
	 * 
	 * 璇︽儏鍙锛歰ut_runnable_jar/command_reference.txt
	 */
	private void setArguments(String[] args) {
		if (args.length > 0)
			this.threadNumber = Integer.parseInt(args[0]); // 浣跨敤浼犲叆鐨勫弬鏁拌祴鍊肩嚎绋嬩釜鏁�
		if (args.length > 1) {
			this.voiceFile = args[1];
			this.checkSetVoiceFormat(this.voiceFile);
		}
		if (args.length > 2) {
			AsrPersonalConfig.engineModelType = EngineModelType.parse(args[2]);
			if (AsrPersonalConfig.engineModelType == EngineModelType._16k_0)
				AsrGlobelConfig.CUT_LENGTH = 8192; // 16K璇煶 涔熻缃垚姣忕鍙�4娆¤姹傦紝浼樺寲婕旂ず鏁堟灉銆�
		}
		if (args.length > 3)
			AsrGlobelConfig.NOTIFY_ALL_RESPONSE = Boolean.parseBoolean(args[3]);
	}

	private void checkSetVoiceFormat(String voiceFile) {
		int index = voiceFile.lastIndexOf(".");
		if (index == -1)
			return;
		String formatName = voiceFile.substring(index + 1).trim().toLowerCase();
		AsrPersonalConfig.voiceFormat = VoiceFormat.parse(formatName);
	}
}

// --------------------------------------------------------------------------------------------------------------
/**
 * 鍗曚釜浠诲姟瀵硅薄绀轰緥銆傚寘鍚簡鏈嶅姟绾跨▼鐨勫垱寤恒�佸惎鍔ㄥ拰鍏抽棴鏂规硶銆�
 * 
 * @author iantang
 * @version 1.0
 */
class VoiceTask {

	private ReceiverEntrance receiverEntrance;
	private int taskId;
	private String voiceFile;
	private VoiceAddingTask voiceAddingTask;

	public VoiceTask(int taskId, String voiceFile) {
		this.taskId = taskId;
		this.voiceFile = voiceFile;
	}

	/**
	 * 鍒涘缓鍜屽惎鍔ㄦ湇鍔＄嚎绋嬨�傚寘鎷細鏁版嵁娣诲姞绾跨▼銆佸彂閫佺嚎绋嬨�侀�氱煡绾跨▼銆�
	 */
	public void start() {
		// 鏂板缓涓�涓湇鍔�
		this.receiverEntrance = new ReceiverEntrance(taskId);
		// 鍚姩鏈嶅姟
		this.receiverEntrance.start();
		// 娉ㄥ唽N涓洖璋僅andler
		this.receiverEntrance.registerReponseHandler(new MyResponseHandler(this.taskId));
		// 寮�濮嬫坊鍔犳暟鎹�
		this.voiceAddingTask = new VoiceAddingTask(this.receiverEntrance, voiceFile);
		this.voiceAddingTask.start();

		// 10绉掑悗鍋滄浠诲姟/鍏抽棴鏈嶅姟銆傚闇�涓�鐩翠娇鐢紝鍒欎笉瑕佽皟鐢ㄥ畠銆�
		/*this.sleepSomeTime(10000);
		this.stop();*/
	}

	public void stop() {
		this.voiceAddingTask.stop();
		this.receiverEntrance.stopService();
	}
}

// --------------------------------------------------------------------------------------------------------------
/**
 * 娣诲姞璇煶鏁版嵁鐨勪换鍔＄被銆傛ā鎷燂細鐙珛绾跨▼锛屾寔缁皢璇煶鐗囨锛堝瓧鑺傛暟鎹級娣诲姞鍒扮紦瀛樸��
 * 
 * @author iantang
 * @version 1.0
 */
class VoiceAddingTask {

	private ReceiverEntrance receiverEntrance;
	private String voiceFile;
	private boolean keepAdding = true;

	public VoiceAddingTask(ReceiverEntrance receiverEntrance, String voiceFile) {
		this.receiverEntrance = receiverEntrance;
		this.voiceFile = voiceFile;
	}

	public void start() {
		Thread thread = new Thread("message sender thread") {
			public void run() {
				repeatAddBytesRequest();
			};
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 鐢ㄦ埛绾跨▼A锛氭寔缁皢璇煶鐗囨锛堝瓧鑺傛暟鎹級娣诲姞鍒扮紦瀛樸�傚叿浣撶殑鍙戦�佸拰閫氱煡鍔ㄤ綔鐢卞叾瀹冪嚎绋嬭礋璐ｃ��
	 * 
	 * 鏈柟娉曚粎浣滅ず渚嬬敤閫旓紝鐢ㄦ埛鍙寜闇�淇敼銆備吉鐮佷緥瀛愶細while(keepAdding){....; receiverEntrance.add(nextBytes); ...;}
	 */
	protected void repeatAddBytesRequest() {
		while (this.keepAdding) {
			// 鏂规硶1锛氭祦寮忚姹傛ā鎷熺ず渚嬨�備笉鏂拷鍔犲皬娈垫暟鎹紝鍦ㄨ闊崇粨鏉熷悗锛屾墠璋冪敤voiceEnd鏂规硶鏍囪瘑缁撳熬銆�
			// 涓嬮潰鍙槸绀轰緥锛屾瘡娆¤皟鐢╝dd鏂规硶鏃舵坊鍔犵殑瀛楄妭鏁扮粍鐨勯暱搴﹀彲鐢辩敤鎴锋牴鎹疄闄呮儏鍐佃嚜琛屾帶鍒讹紝澶у皬涓嶉檺銆�
			int subLength = AsrGlobelConfig.CUT_LENGTH / 2; // 鍦ㄥ綋鍓嶇ず渚嬩腑锛屾鍊间负锛�2048锛屾垨4096
			List<byte[]> list = ByteUtils.subToSmallBytes(new File(this.voiceFile), subLength);
			for (int i = 0; i < list.size() - 1; i++) {
				receiverEntrance.add(list.get(i));
				sleepSomeTime(125); // 瀵逛簬8K鐨勮闊筹紝姣忕鍙戝嚭2048*8 = 16KB鏁版嵁锛屼笌瀹為檯鐩哥銆�
			}
			receiverEntrance.add(list.get(list.size() - 1)); // add last bytes
			receiverEntrance.voiceEnd();

			// 鏂规硶2锛氭坊鍔犱竴鏁村彞璇濓紝涓旀爣璇嗙粨灏撅細
			/*byte[] content = ByteUtils.inputStream2ByteArray("test_wav/8k.wav");
			// 涓嬮潰鐨勪袱鍙ヨ瘽锛屽彲浠ョ敤 receiverEntrance.add(content, true);鏉ヤ唬鏇裤��
			receiverEntrance.add(content);
			receiverEntrance.voiceEnd();*/

			this.stop(); // 涓烘柟渚挎紨绀烘湰瀹炰緥锛屼笉鍋氬惊鐜彂閫侊紝鎵�浠ュ湪姝ゅ仠姝€��
		}
	}

	/**
	 * 鍋滄娣诲姞绾跨▼锛堢敤鎴风嚎绋婣锛�
	 */
	public void stop() {
		this.keepAdding = false;
	}

	private void sleepSomeTime(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}

// --------------------------------------------------------------------------------------------------------------
/**
 * 鐢ㄦ埛鑷繁鍐欑殑鍥炶皟Handler.琚玁otifyService鏈嶅姟绾跨▼锛堝彲鐞嗚В涓虹敤鎴风嚎绋婤锛夎皟鐢ㄣ��
 */
class MyResponseHandler implements FlowHandler {

	private int handlerId;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public MyResponseHandler(int handlerId) {
		this.handlerId = handlerId;
	}

	/**
	 * 鍥炲鏁版嵁閫氳繃姝ゆ柟娉曢�氱煡鍒扮敤鎴枫��
	 */
	@Override
	public void onUpdate(Object... params) {
		// VadResponse response = (VadResponse) params[0]; // Vad鐗堢敤鎴疯鐢ㄦ琛屼唬鏇夸笅闈竴琛�
		VoiceResponse response = (VoiceResponse) params[0];

		// Your own logic.
		System.out.println(sdf.format(new Date()) + " Handler_" + this.handlerId + ", received response -->: "
				+ response.getOriginalText());
		/*System.out.println(JacksonUtil.toJsonString(voiceResponse));*/

		// 鍙互鏌ョ湅寤惰繜銆�
		this.printDelay(response);

		// 鍙互鎻愬彇鍑哄綋鍓峍ad鏂彞鍥炲銆傞�傜敤浜嶸ad鐗堢敤鎴凤紝绾夸笂鐢ㄦ埛璇峰拷鐣ャ��
		/*if (response.getResultList() != null) {
			for (VadResult vadResult : response.getResultList()) {
				System.out.println("Received vad response: " + vadResult.getVoiceTextStr());
			}
		}*/
	}

	/**
	 * 鏌ョ湅鍜屾墦鍗板欢杩熶俊鎭�傚欢杩熺粺璁℃柟娉曞彲浠庨」鐩甦ocs鐩綍涓煡鐪嬶紝鎴栨祻瑙堜笅闈㈢殑鍚箟瑙ｉ噴銆�
	 * 
	 * <pre>
	 * 瀹炰緥濡備笅锛�
	 * <<<End_Cut>>> write delay: 103 ms, node delay: 103 ms, notify delay: 105 ms. Pre average write: 101 ms, node: 102 ms.
	 * 鍒嗗埆琛ㄧず锛氬彂閫佸欢杩熴�佽妭鐐瑰欢杩熴�侀�氱煡寤惰繜銆傚墠闈涓垎鐗囩殑骞冲潎鍙戦�併�佸钩鍧囪妭鐐瑰欢杩熴��
	 * 
	 * 濡傞渶娴嬭瘯璇煶鍙戝畬鍚庡涔呰兘鏀跺畬鍏ㄩ儴璇嗗埆缁撴灉锛屽彲浠庯細鈥�<<<End_Cut>>>鈥� 涓殑notify delay鑾峰緱鍙傝�冦�傚缓璁互write Delay浣滀负鏈嶅姟鎬ц兘鑰冮噺銆�
	 * 
	 * 寤惰繜鍚箟瑙ｉ噴锛�
	 * WriteDelay锛�    鏁版嵁鏀跺彂鍜岀綉缁滃欢杩�+瑙ｆ瀽Delay(1-2ms)銆� 
	 * NodeDelay锛�      鏁版嵁婊炵暀寤惰繜 + WriteDelay銆�   鍗筹細浠庡鎴穉dd瀹屾垚1涓垎鐗囨暟鎹紑濮嬶紝鑷冲垎鐗囩粨鏋滄敹鍒颁负姝紝鏈熼棿鎬荤殑鏃堕棿娑堣�椼��
	 * NotifyDelay锛� NodeDelay + 瀹㈡埛onHander Delay(澶勭悊鍥炲鑰楁椂)銆傛鍊艰嫢澶т簬NodeDelay涓斿湪澧為暱锛屼細瀵艰嚧Response鍫嗙Н锛屾渶缁堝唴瀛樻孩鍑恒��
	 * </pre>
	 */
	private void printDelay(VoiceResponse voiceResponse) {
		TimeStat timeStat = voiceResponse.getTimeStat();
		if (voiceResponse.isEndCut()) {
			this.printDelay("<<<End_Cut>>>", timeStat);
		} else {
			this.printDelay("<<<Middle_Cut>>>", timeStat);
		}
	}

	private void printDelay(String cutType, TimeStat timeStat) {
		System.out.println(sdf.format(new Date()) + " " + cutType + " write delay: " + timeStat.getWriteDelay()
				+ " ms, node delay: " + timeStat.getNodeDelay() + " ms, notify delay: " + timeStat.getNotifyDelay()
				+ " ms. Pre average write: " + timeStat.getPreAverageWriteDelay() + " ms, node: "
				+ timeStat.getPreAverageNodeDelay() + " ms.");
	}
}
