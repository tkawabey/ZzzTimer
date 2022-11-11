#include <string.h>
#include <jni.h>
#include "dtfm.h"
#include <stdio.h>
#include "four_non_bronds_yyys_zzztimer_util_DTFM.h"
#include "inflate_buff.h"
#include "ogg_writer.h"
#include "encode_file.h"

#include <android/log.h>

#define  LOG_TAG    "zzztimer.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)



typedef struct _MyDTFMINFO
{
	dtfm_chanel		chanel;
	
	dtfm_judge		judge;

	unsigned char*	m_p_save_buf;
	int				m_save_buf_size;
	int				m_cur_buf_size;

	int				m_noise_less_msec;

	char*			m_p_txt_buf;
	int				m_txt_buf_size;
	int				m_cur_txt_size;

	char*			m_p_analyzed_buf;

	OggInfo*		m_pOggInfo;
}MyDTFMINFO;

//
//	チャンネルの初期化
//
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_initDefaultChanel
  (JNIEnv *env, jclass cls, jobject obj_chanel)
{
	MyDTFMINFO*	p_dtfm_inf = NULL;


	jclass clsj = env->GetObjectClass(obj_chanel);

	jfieldID fj_chanelHandle = env->GetFieldID(clsj, "chanelHandle", "J");
	if (fj_chanelHandle == NULL) {
		return;
	}

	// 
	jlong l_adder = env->GetLongField(obj_chanel, fj_chanelHandle);
	if( l_adder != 0 ) {
		p_dtfm_inf = (MyDTFMINFO*)l_adder;
		free(p_dtfm_inf);
	}

	p_dtfm_inf = (MyDTFMINFO*)malloc(sizeof(MyDTFMINFO));
	if( p_dtfm_inf == NULL ) {
		return ;
	}

	
	dtfm_init_default_chanel( &p_dtfm_inf->chanel );
	dtfm_init_judge_data( &p_dtfm_inf->chanel, &p_dtfm_inf->judge );


	l_adder = (jlong)p_dtfm_inf;
	env->SetLongField(obj_chanel, fj_chanelHandle, l_adder);

	p_dtfm_inf->m_cur_buf_size = 0;
	p_dtfm_inf->m_save_buf_size = 
		p_dtfm_inf->chanel.sample_bit_per_sec
		* 4
		* (p_dtfm_inf->chanel.sample_bits/8)
		;
	p_dtfm_inf->m_p_save_buf = 
		(unsigned char*)malloc( p_dtfm_inf->m_save_buf_size );
	p_dtfm_inf->m_noise_less_msec = 0;


	p_dtfm_inf->m_txt_buf_size = 256;
	p_dtfm_inf->m_cur_txt_size = 0;
	p_dtfm_inf->m_p_txt_buf = (char*)malloc( p_dtfm_inf->m_txt_buf_size );
	memset(p_dtfm_inf->m_p_txt_buf, 0, p_dtfm_inf->m_txt_buf_size );


	p_dtfm_inf->m_p_analyzed_buf = NULL;
}
/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    resetChanel
 * Signature: (Lfour/non/bronds/yyys/pipoparoid/util/DTFM;)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_resetChanel
  (JNIEnv *env, jclass cls, jobject obj_chanel)
{
	MyDTFMINFO*	p_dtfm_inf = NULL;


	jclass clsj = env->GetObjectClass(obj_chanel);

	jfieldID fj_chanelHandle = env->GetFieldID(clsj, "chanelHandle", "J");
	if (fj_chanelHandle == NULL) {
		return;
	}
	// 
	jlong l_adder = env->GetLongField(obj_chanel, fj_chanelHandle);
	if( l_adder != 0 ) {
		p_dtfm_inf = (MyDTFMINFO*)l_adder;
		dtfm_destroy_judge_data( &p_dtfm_inf->judge );
        if( p_dtfm_inf->m_p_save_buf != NULL ) {
            free( p_dtfm_inf->m_p_save_buf );
        }
        p_dtfm_inf->m_p_save_buf = NULL;
        
        
        dtfm_init_judge_data( &p_dtfm_inf->chanel, &p_dtfm_inf->judge );


        p_dtfm_inf->m_cur_buf_size = 0;
        p_dtfm_inf->m_save_buf_size = 
            p_dtfm_inf->chanel.sample_bit_per_sec
            * 4
            * (p_dtfm_inf->chanel.sample_bits/8)
            ;
        p_dtfm_inf->m_p_save_buf = 
            (unsigned char*)malloc( p_dtfm_inf->m_save_buf_size );
        p_dtfm_inf->m_noise_less_msec = 0;


        p_dtfm_inf->m_txt_buf_size = 256;
        p_dtfm_inf->m_cur_txt_size = 0;
        p_dtfm_inf->m_p_txt_buf = (char*)malloc( p_dtfm_inf->m_txt_buf_size );
        memset(p_dtfm_inf->m_p_txt_buf, 0, p_dtfm_inf->m_txt_buf_size );


        p_dtfm_inf->m_p_analyzed_buf = NULL;
	}
}
//
//	チャンネルの開放
//
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_destroyDefaultChanel
  (JNIEnv *env, jclass cls, jobject obj_chanel)
{
	MyDTFMINFO*	p_dtfm_inf = NULL;


	jclass clsj = env->GetObjectClass(obj_chanel);

	jfieldID fj_chanelHandle = env->GetFieldID(clsj, "chanelHandle", "J");
	if (fj_chanelHandle == NULL) {
		return;
	}

	// 
	jlong l_adder = env->GetLongField(obj_chanel, fj_chanelHandle);
	if( l_adder != 0 ) {
		p_dtfm_inf = (MyDTFMINFO*)l_adder;

		dtfm_destroy_judge_data( &p_dtfm_inf->judge );

		free(p_dtfm_inf);
	}

	if( p_dtfm_inf->m_p_save_buf != NULL ) {
		free( p_dtfm_inf->m_p_save_buf );
	}
}




/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getSampleBitPerSec
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getSampleBitPerSec
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jint)p_dtfm_inf->chanel.sample_bit_per_sec;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setSampleBitPerSec
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setSampleBitPerSec
  (JNIEnv *env, jclass cls, jlong addr, jint val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.sample_bit_per_sec = val;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getSampleBits
 * Signature: (J)S
 */
JNIEXPORT jshort JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getSampleBits
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jshort)p_dtfm_inf->chanel.sample_bits;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setSampleBits
 * Signature: (JS)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setSampleBits
  (JNIEnv *env, jclass cls, jlong addr, jshort val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.sample_bits = val;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getChanel
 * Signature: (J)S
 */
JNIEXPORT jshort JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getChanel
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jshort)p_dtfm_inf->chanel.chanel;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setChanel
 * Signature: (JS)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setChanel
  (JNIEnv *env, jclass cls, jlong addr, jshort val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.chanel = val;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getSingnalMsec
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getSingnalMsec
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jint)p_dtfm_inf->chanel.signal_msec;
}


/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setSingnalMsec
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setSingnalMsec
  (JNIEnv *env, jclass cls, jlong addr, jint val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.signal_msec = val;
}


/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getJudgeMsec
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getJudgeMsec
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jint)p_dtfm_inf->chanel.judge_msec;
}


/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setJudgeMsec
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setJudgeMsec
  (JNIEnv *env, jclass cls, jlong addr, jint val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.judge_msec = val;
}


/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getBlankMsec
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getBlankMsec
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return -1;
	}
	return (jint)p_dtfm_inf->chanel.blank_msec;
}



/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    setBlankMsec
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_setBlankMsec
  (JNIEnv *env, jclass cls, jlong addr, jint val)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	if( p_dtfm_inf == NULL ) {
		return ;
	}
	p_dtfm_inf->chanel.blank_msec = val;
}


/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    getWaheFileHeaderM
 * Signature: (JI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_getWaveFileHeader
  (JNIEnv *env, jclass cls, jlong addr,  jint data_size)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	unsigned char head_buf[WAVE_FILE_HEADER_SIZE];
	jbyteArray dstj = NULL;
	jbyte* dst = NULL;
	int i;
	
	
	if( p_dtfm_inf == NULL ) {
		goto END_;
	}

	// WAVE ファイルのヘッダを書き込み
	dtfm_wave_file_header(&p_dtfm_inf->chanel,
		data_size,
		head_buf, 0, 0);


	dstj = env->NewByteArray(WAVE_FILE_HEADER_SIZE);
	if( dstj == NULL ) {
		goto END_;
	}
    dst = env->GetByteArrayElements(dstj, NULL);
    if (dst == NULL) {
        goto END_;
    }
    for(i = 0; i < WAVE_FILE_HEADER_SIZE; i++){
        dst[i] = head_buf[i];
    }
END_:
	if( dst != NULL ) {
		env->ReleaseByteArrayElements(dstj, dst, 0);
	}
	return dstj;
}



/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    updateWaveFileHeaderFile
 * Signature: (JILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_updateWaveFileHeaderFile
  (JNIEnv *env, jclass cls, jlong addr, jint jdata_size, jstring jfile_path, jobjectArray tags)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	const char* p_file_path;
	unsigned int data_size = (unsigned int)jdata_size;
	int ret = -1;
	int i, j;
	wave_list_chunk_data*	p_chunk_data = NULL;
	jsize					tag_cnt = 0;
//	int						i_chunk_cnt = 0;

	// リストチャンクの情報作成
	if( tags != NULL ) {
		tag_cnt = env->GetArrayLength(tags);
		if( tag_cnt != 0 ) {
			p_chunk_data = new wave_list_chunk_data[tag_cnt];
			memset(p_chunk_data, 0, sizeof(wave_list_chunk_data)*tag_cnt);
			for(i = 0; i < tag_cnt; i++ ) {
				jobject elemj = env->GetObjectArrayElement(tags, i);
				jclass clsj = env->GetObjectClass(elemj);

				jmethodID id_getName = env->GetMethodID(clsj, "getName", "()Ljava/lang/String;");
				jmethodID id_getValue = env->GetMethodID(clsj, "getValue", "()Ljava/lang/String;");

				jstring jName   = (jstring)env->CallObjectMethod(elemj, id_getName);
				jstring jValue  = (jstring)env->CallObjectMethod(elemj, id_getValue);

				const char *p_name = env->GetStringUTFChars(jName, NULL);
				const char *p_value = env->GetStringUTFChars(jValue, NULL);

LOGI("TAG:%s  VALUE:%s", p_name, p_value);


				strncpy(p_chunk_data[i].name, p_name, sizeof(p_chunk_data[i].name));

				p_chunk_data[i].data = (void*)malloc( strlen(p_value)+1 );
				memset(p_chunk_data[i].data, 0, strlen(p_value)+1);
				strcpy((char*)p_chunk_data[i].data, p_value);
				p_chunk_data[i].data_size = strlen((const char*)p_chunk_data[i].data);


				env->ReleaseStringUTFChars(jName, p_name);
				env->ReleaseStringUTFChars(jValue, p_value);
			}
		}

/*
		i_chunk_cnt = env->GetArrayLength(listChanks);
		i_chunk_cnt /= 2;
		if( i_chunk_cnt != 0 ) {
			j = 0;
			p_chunk_data = new wave_list_chunk_data[i_chunk_cnt];
			memset(p_chunk_data, 0, sizeof(wave_list_chunk_data)*i_chunk_cnt);
			for(i = 0; i < i_chunk_cnt; i++) {
				jstring objName = (jstring)env->GetObjectArrayElement(listChanks, j);
				j++;
				jstring objData = (jstring)env->GetObjectArrayElement(listChanks, j);
				j++;

				const char* p_c_name = env->GetStringUTFChars(objName, NULL);
				strncpy(p_chunk_data[i].name, p_c_name, sizeof(p_chunk_data[i].name));
				env->ReleaseStringUTFChars(objName, p_c_name);

				const char* p_data = env->GetStringUTFChars(objData, NULL);
				p_chunk_data[i].data = (void*)malloc( strlen(p_data)+1 );
				memset(p_chunk_data[i].data, 0, strlen(p_data)+1);
				strcpy((char*)p_chunk_data[i].data, p_data);
				p_chunk_data[i].data_size = strlen((const char*)p_chunk_data[i].data);
				env->ReleaseStringUTFChars(objData, p_data);
			}
		}
*/
	}



	p_file_path = env->GetStringUTFChars(jfile_path, NULL);
	if( p_file_path == NULL ) {
		goto END_;
	}
	ret = dtfm_update_wave_file_header_file(&p_dtfm_inf->chanel, data_size, p_chunk_data, tag_cnt, p_file_path);
	env->ReleaseStringUTFChars(jfile_path, p_file_path);
END_:
	for(i = 0; i < tag_cnt; i++) {
		if( p_chunk_data[i].data != NULL ) {
			free( p_chunk_data[i].data );
		}
	}
	if( p_chunk_data != NULL ) {
		delete []p_chunk_data;
	}
	return ret;
}


/*　文字列から、DTFMデータ(PCM)を作成
 * 
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    string2PCM
 * Signature: (JLjava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_string2PCM
  (JNIEnv *env, jclass cls, jlong addr, jstring text)
{
	const char *src;
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	int buff_size = 0;
	int i;
	unsigned char* p_buff = NULL;
	jbyteArray dstj = NULL;
	jbyte* dst = NULL;

	
	if( p_dtfm_inf == NULL ) {
		goto END_;
	}

	src = env->GetStringUTFChars(text, NULL);
	if( src == NULL ) {
		goto END_;
	}
	
	buff_size = dtfm_text_to_pcm(&p_dtfm_inf->chanel, src, NULL, 0);
	if( buff_size < 0 ) {
		goto END_;
	}
	p_buff = (unsigned char*)malloc(buff_size+WAVE_FILE_HEADER_SIZE);
	if( p_buff == NULL ) {
		goto END_;
	}

	// WAVE ファイルのヘッダを書き込み
	dtfm_wave_file_header(&p_dtfm_inf->chanel,
		buff_size,
		p_buff, 0, 0);

	// 
	dtfm_text_to_pcm(&p_dtfm_inf->chanel, src, &p_buff[WAVE_FILE_HEADER_SIZE], buff_size);

	dstj = env->NewByteArray(buff_size+WAVE_FILE_HEADER_SIZE);
	if( dstj == NULL ) {
		goto END_;
	}
    dst = env->GetByteArrayElements(dstj, NULL);
    if (dst == NULL) {
        goto END_;
    }
    for(i = 0; i < buff_size+WAVE_FILE_HEADER_SIZE; i++){
        dst[i] = p_buff[i];
    }


END_:
	if( src != NULL ) {
		env->ReleaseStringUTFChars(text, src);
	}
	if( dst != NULL ) {
		env->ReleaseByteArrayElements(dstj, dst, 0);
	}
	if( p_buff != NULL ) {
		free( p_buff );
	}
	return dstj;
}




static void analye_DTFM_cb_from_rl(char c, void* p_user_data)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)p_user_data;

	if( p_dtfm_inf->m_cur_txt_size > p_dtfm_inf->m_txt_buf_size+10) {
		int new_size = p_dtfm_inf->m_txt_buf_size + 256;
		char* new_buff = (char*)malloc( new_size );
		memset(new_buff, 0, new_size);
		memcpy(new_buff, p_dtfm_inf->m_p_txt_buf, p_dtfm_inf->m_cur_txt_size);

		free(p_dtfm_inf->m_p_txt_buf);
		p_dtfm_inf->m_p_txt_buf = new_buff;
		p_dtfm_inf->m_txt_buf_size = new_size;
	}

	p_dtfm_inf->m_p_txt_buf[ p_dtfm_inf->m_cur_txt_size ] = c;

	p_dtfm_inf->m_cur_txt_size++;
}



/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    analyzeDTFM
 * Signature: ([SI)Ljava/lang/String;
 */
JNIEXPORT jint JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_analyzeDTFM
  (JNIEnv *env, jclass cls, jlong addr, jshortArray data_array, jint array_size)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	int size = array_size * 2;
	int analized_size = 0;
	int ret = 0;
	unsigned char* p_buff = NULL;


    p_buff = (unsigned char*)env->GetShortArrayElements(data_array, NULL);
    if (p_buff == NULL) {
        goto END_;
    }



	memcpy(&p_dtfm_inf->m_p_save_buf[p_dtfm_inf->m_cur_buf_size], p_buff, size);
	p_dtfm_inf->m_cur_buf_size += size;

	ret = dtfm_pcm_to_text(
		&(p_dtfm_inf->chanel),
		&(p_dtfm_inf->judge),
		analye_DTFM_cb_from_rl,
		p_dtfm_inf,
		p_dtfm_inf->m_p_save_buf,
		p_dtfm_inf->m_cur_buf_size,
		&analized_size);

	if( analized_size != 0 ) {
		if( p_dtfm_inf->m_cur_buf_size > analized_size ) {
			p_dtfm_inf->m_cur_buf_size -= analized_size;
			memmove(p_dtfm_inf->m_p_save_buf, &p_dtfm_inf->m_p_save_buf[analized_size], p_dtfm_inf->m_cur_buf_size);
		} else {
			p_dtfm_inf->m_cur_buf_size = 0;
		}


		if( ret == 0 ) {
			// 無音が1秒間つづいたら、文字バッファに溜まった、文字列を出力する。
			p_dtfm_inf->m_noise_less_msec += analized_size;
			if( p_dtfm_inf->m_noise_less_msec > (p_dtfm_inf->chanel.sample_bit_per_sec*(p_dtfm_inf->chanel.sample_bits/8)) ) {

			}
		}

	} 
END_:

	if( p_buff != NULL ) {
		env->ReleaseShortArrayElements(data_array, (short*)p_buff, 0);
	}
	return p_dtfm_inf->m_cur_txt_size;
}






void analyze_call_back(char c, void* p_user_data)
{
	inflate_buff* p_buff = (inflate_buff*)p_user_data;
	if( p_buff != NULL ) {
		p_buff->add( c );
	}
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    analyzeDTFMFile
 * Signature: (JLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_analyzeDTFMFile
  (JNIEnv *env, jclass cls, jlong addr, jstring jfile_path)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	int ret = 0;
	inflate_buff		buff(255, 255);
	const char *p_file_path = env->GetStringUTFChars(jfile_path, NULL);
	jstring return_val = NULL;

	ret = dtfm_pcm_to_text_from_file(p_file_path,
		analyze_call_back,
		&buff);
	if( ret < 0 ) {
		char msg[500];

		sprintf(msg, "ErrorCode : %d FILE:[%s]", ret, p_file_path);
		return_val = env->NewStringUTF( msg );
		goto END_;
	}

	return_val = env->NewStringUTF( buff.str() );

	goto END_;
END_:
	env->ReleaseStringUTFChars(jfile_path, p_file_path);

	return return_val;
}

static void dtfm_parse_buffring_callback(const char* str, void* p_user_data)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)p_user_data;

	p_dtfm_inf->m_p_analyzed_buf = strdup( str );


}




/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    analyzeDTFMBuffring
 * Signature: (J[BI)Ljava/lang/String;
 */
JNIEXPORT void JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_analyzeDTFMBuffring
  (JNIEnv *env, jclass cls, jlong addr, jbyteArray jbuf, jint buf_size, jobject jcallcb)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	jstring return_val = NULL;
	int ret = 0;
	unsigned char* p_buff = NULL;


    p_buff = (unsigned char*)env->GetByteArrayElements(jbuf, NULL);
    if (p_buff == NULL) {
        goto END_;
    }
	
	p_dtfm_inf->m_p_analyzed_buf = NULL;

	ret = dtfm_buffring_pcm_to_text(
		&p_dtfm_inf->chanel,
		&p_dtfm_inf->judge,
		dtfm_parse_buffring_callback,
		p_dtfm_inf,
		p_buff,
		buf_size);

	{
		jclass clsj = env->GetObjectClass(jcallcb);
		jmethodID mj = env->GetMethodID(clsj, "readedCB", "(Ljava/lang/String;)V");

		return_val = env->NewStringUTF( "Java_four_non_bronds_yyys_zzztimer_util_DTFM_analyzeDTFMBuffring" );


		env->CallVoidMethod(jcallcb, mj, return_val);
		env->DeleteLocalRef( return_val );
		return_val = NULL;
	}
	if( p_dtfm_inf->m_p_analyzed_buf != NULL ) {
		

		jclass clsj = env->GetObjectClass(jcallcb);
		jmethodID mj = env->GetStaticMethodID(clsj, "readedCB", "(Ljava/lang/String;)V");

		return_val = env->NewStringUTF( p_dtfm_inf->m_p_analyzed_buf);


		env->CallVoidMethod(jcallcb, mj, return_val);


		free( p_dtfm_inf->m_p_analyzed_buf );
		p_dtfm_inf->m_p_analyzed_buf = NULL;
	}
END_:
	if( return_val != NULL ) {
		env->DeleteLocalRef( return_val );
	}
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    encExtString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_nEncExtString
  (JNIEnv *env, jclass cls, jstring jstr)
{
	jstring return_val = NULL;
	char* p_dst = NULL;
	const char *p_str = env->GetStringUTFChars(jstr, NULL);
	int len = 0;

	len = strlen(p_str)*2+1;
	p_dst = (char*)malloc(len);
	if( p_dst == NULL ) {
		goto END_;
	}
	memset(p_dst, 0, len);
	enc_ext_dtfm_charactor(p_str, p_dst, len);
END_:
	if( p_dst != NULL ) {
		return_val = env->NewStringUTF( p_dst );
		free( p_dst );
	}
	return return_val;
}

/*
 * Class:     four_non_bronds_yyys_pipoparoid_util_DTFM
 * Method:    decExtString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_nDecExtString
  (JNIEnv *env, jclass cls, jstring jstr)
{
	jstring return_val = NULL;
	char* p_dst = NULL;
	const char *p_str = env->GetStringUTFChars(jstr, NULL);
	int len = 0;

	len = strlen(p_str)*2+1;
	p_dst = (char*)malloc(len);
	if( p_dst == NULL ) {
		goto END_;
	}
	memset(p_dst, 0, len);
	dec_ext_dtfm_charactor(p_str, p_dst, len);
END_:
	if( p_dst != NULL ) {
		return_val = env->NewStringUTF( p_dst );
		free( p_dst );
	}
	return return_val;
}




/*
 * Class:     four_non_bronds_yyys_zzztimer_util_DTFM
 * Method:    openWriteOgg
 * Signature: (JLjava/lang/String;Ljava/util/HashMap;)Z
 */
JNIEXPORT jboolean JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_openWriteOgg
  (JNIEnv *env, jclass cls, jlong addr, jstring jfile_path, jobjectArray tags)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	const char* p_file_path = NULL;
	OggInfoTag*	p_tags = NULL;
	jsize		tag_cnt = 0;
	jsize		i = 0;
	
	if( tags != NULL ) {
		tag_cnt = env->GetArrayLength(tags);
		if( tag_cnt != 0 ) {
			p_tags = new OggInfoTag[tag_cnt];
			memset(p_tags, 0, sizeof(OggInfoTag)*tag_cnt);
//LOGI("tag_cnt:%d", tag_cnt);
			for(i = 0; i < tag_cnt; i++ ) {
				jobject elemj = env->GetObjectArrayElement(tags, i);
				jclass clsj = env->GetObjectClass(elemj);

				jmethodID id_getName = env->GetMethodID(clsj, "getName", "()Ljava/lang/String;");
				jmethodID id_getValue = env->GetMethodID(clsj, "getValue", "()Ljava/lang/String;");

				jstring jName   = (jstring)env->CallObjectMethod(elemj, id_getName);
				jstring jValue  = (jstring)env->CallObjectMethod(elemj, id_getValue);

				const char *p_name = env->GetStringUTFChars(jName, NULL);
				const char *p_value = env->GetStringUTFChars(jValue, NULL);
				

//				LOGI("p_name:%s : %s", p_name, p_value);


				p_tags[i].p_name = new char[1+strlen(p_name)];
				strcpy(p_tags[i].p_name, p_name);

				p_tags[i].p_value = new char[1+strlen(p_value)];
				strcpy(p_tags[i].p_value, p_value);


				env->ReleaseStringUTFChars(jName, p_name);
				env->ReleaseStringUTFChars(jValue, p_value);
			}
		}
	}


	p_file_path = env->GetStringUTFChars(jfile_path, NULL);
	if( p_file_path == NULL ) {
		goto END_;
	}


	p_dtfm_inf->m_pOggInfo = ogg_init(p_dtfm_inf->chanel.chanel, p_file_path, p_tags, (int)tag_cnt);



END_:
	if( p_tags != 0 ) {
		for(i = 0; i < tag_cnt; i++ ) {
			if( p_tags[i].p_name ) {
				delete []p_tags[i].p_name;	
			}
			if( p_tags[i].p_value ) {
				delete []p_tags[i].p_value;	
			}
		}
		delete []p_tags;
	}
	if( p_file_path != NULL ) {
		env->ReleaseStringUTFChars(jfile_path, p_file_path);
	}
	return (jboolean)true;
}

/*
 * Class:     four_non_bronds_yyys_zzztimer_util_DTFM
 * Method:    closeWriteOgg
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_closeWriteOgg
  (JNIEnv *env, jclass cls, jlong addr)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;

	if( p_dtfm_inf->m_pOggInfo != NULL ) {
		ogg_close(p_dtfm_inf->m_pOggInfo);
		p_dtfm_inf->m_pOggInfo = NULL;
	}

	return (jboolean)true;
}

/*
 * Class:     four_non_bronds_yyys_zzztimer_util_DTFM
 * Method:    writeOgg
 * Signature: (J[BI)Z
 */
JNIEXPORT jboolean JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_writeOgg
  (JNIEnv *env, jclass cls, jlong addr, jbyteArray jbuf, jint data_size)
{
	MyDTFMINFO*	p_dtfm_inf = (MyDTFMINFO*)addr;
	signed char* p_buff = NULL;


    p_buff = (signed char*)env->GetByteArrayElements(jbuf, NULL);
    if (p_buff == NULL) {
        goto END_;
    }
	ogg_write(p_dtfm_inf->m_pOggInfo, p_buff, data_size);

END_:
	if( p_buff != NULL ) {
		env->ReleaseByteArrayElements(jbuf, (jbyte*)p_buff, 0);
	}
	return (jboolean)true;
}
/*
 * Class:     four_non_bronds_yyys_zzztimer_util_DTFM
 * Method:    convertOgg
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_four_non_bronds_yyys_zzztimer_util_DTFM_convertOgg
  (JNIEnv *env, jclass cls, jstring jInFile, jstring jOutFile)
{
	const char* p_in_file_path = NULL;
	const char* p_out_file_path = NULL;


	p_in_file_path = env->GetStringUTFChars(jInFile, NULL);
	if( p_in_file_path == NULL ) {
		goto END_;
	}


	p_out_file_path = env->GetStringUTFChars(jOutFile, NULL);
	if( p_out_file_path == NULL ) {
		goto END_;
	}


	ogg_encode_file(p_in_file_path, p_out_file_path);



END_:
	if( p_in_file_path != NULL ) {
		env->ReleaseStringUTFChars(jInFile, p_in_file_path);
	}
	if( p_out_file_path != NULL ) {
		env->ReleaseStringUTFChars(jOutFile, p_out_file_path);
	}
}
