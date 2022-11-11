#include "ogg_writer.h"
#include <android/log.h>

#define  LOG_TAG    "ogg_writer.c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

OggInfo*	ogg_init(int ch, const char* p_out, OggInfoTag* p_tags, int tag_cnt)
{
//	LOGI("ogg_init %d  p_out-%s", ch, p_out);
	OggInfo* pOggInfo = NULL;
	int ret = 0;
	int i;

	pOggInfo = (OggInfo*)malloc(sizeof(OggInfo));
	if( pOggInfo == NULL ) {
		return NULL;
	}
	memset(pOggInfo, 0, sizeof(OggInfo));

	pOggInfo->p_file_pout = fopen(p_out, "wb");
	if( pOggInfo->p_file_pout == NULL ) {
		free(pOggInfo);
		return NULL;
	}
	pOggInfo->ch = ch;

	/********** Encode setup ************/
	vorbis_info_init(&(pOggInfo->vi));
	/* choose an encoding mode.  A few possibilities commented out, one actually used: */
	ret=vorbis_encode_init_vbr(&(pOggInfo->vi),ch,22050,0.1);
	if(ret) {
		free(pOggInfo);
		return NULL;
	}
	/* add a comment */
	vorbis_comment_init(&(pOggInfo->vc));
	for(i = 0; i < tag_cnt; i++) {
		vorbis_comment_add_tag(&(pOggInfo->vc),p_tags[i].p_name,p_tags[i].p_value);
	}

	/* set up the analysis state and auxiliary encoding storage */
	vorbis_analysis_init(&(pOggInfo->vd),&(pOggInfo->vi));
	vorbis_block_init(&(pOggInfo->vd),&(pOggInfo->vb));

	/* set up our packet->stream encoder */
	/* pick a random serial number; that way we can more likely build
	 chained streams just by concatenation */
	srand(time(NULL));
	ogg_stream_init(&(pOggInfo->os),rand());

	/*	Vorbis streams begin with three headers; the initial header (with
		most of the codec setup parameters) which is mandated by the Ogg
		bitstream spec.  The second header holds any comment fields.  The
		third header holds the bitstream codebook.  We merely need to
		make the headers, then pass them to libvorbis one at a time;
		libvorbis handles the additional Ogg bitstream constraints */

	{
		ogg_packet header;
		ogg_packet header_comm;
		ogg_packet header_code;

		vorbis_analysis_headerout(&(pOggInfo->vd),&(pOggInfo->vc),&header,&header_comm,&header_code);
		ogg_stream_packetin(&(pOggInfo->os),&header); /* automatically placed in its own page */
		ogg_stream_packetin(&(pOggInfo->os),&header_comm);
		ogg_stream_packetin(&(pOggInfo->os),&header_code);

		/* This ensures the actual
		 * audio data will start on a new page, as per spec
		 */
		while(1){
			int result=ogg_stream_flush(&(pOggInfo->os),&(pOggInfo->og));
			if(result==0) {
//				LOGI("break ogg_stream_flush");
				break;
			}
			fwrite(pOggInfo->og.header,1,pOggInfo->og.header_len, pOggInfo->p_file_pout);
			fwrite(pOggInfo->og.body,  1,pOggInfo->og.body_len, pOggInfo->p_file_pout);
		}
	}
	pOggInfo->eos=0;

	return pOggInfo;
}


void		ogg_close(OggInfo* pOggInfo)
{
	if( pOggInfo == NULL ) {
		return ;
	}
	ogg_write(pOggInfo, (signed char*)pOggInfo, 0);


	ogg_stream_clear( &(pOggInfo->os) );
	vorbis_block_clear( &(pOggInfo->vb) );
	vorbis_dsp_clear( &(pOggInfo->vd) );
	vorbis_comment_clear( &(pOggInfo->vc) );
	vorbis_info_clear( &(pOggInfo->vi) );
	fclose( pOggInfo->p_file_pout );
	free(pOggInfo);

}

#define READ 1024

void ogg_write(OggInfo* pOgg, signed char* p_buff, long bytes_length)
{
//	LOGI("ogg_write %p  p_out-%d", pOgg, bytes_length);
	long i;
	long bytes = 0;




	while(!pOgg->eos){

		if( bytes_length > READ*2*pOgg->ch ) {
			bytes = READ*2*pOgg->ch;
		} else {
			bytes = bytes_length; 
		}
//LOGI("1 %d  bytes:%d", pOgg->ch, bytes);

		if(bytes==0){
			/* end of file.  this can be done implicitly in the mainline,
			but it's easier to see here in non-clever fashion.
			Tell the library we're at end of stream so that it can handle
			the last frame and mark end of stream in the output properly */
			vorbis_analysis_wrote(&(pOgg->vd),0);
		} else {
			/* data to encode */

			/* expose the buffer to submit data */
			float **buffer=vorbis_analysis_buffer(&(pOgg->vd),READ);

			/* uninterleave samples */
			//two channels
			if(pOgg->ch==2)
			{
				for(i=0;i<bytes/4;i++){
					buffer[0][i]=((p_buff[i*4+1]<<8)|
								(0x00ff&(int)p_buff[i*4]))/32768.f;
					buffer[1][i]=((p_buff[i*4+3]<<8)|
								(0x00ff&(int)p_buff[i*4+2]))/32768.f;
				}
			}
			else
			{
				//one channel
				for(i=0;i<bytes/2;i++){
					buffer[0][i]=((p_buff[i*2+1]<<8)|
								  (0x00ff&(int)p_buff[i*2]))/32768.f;        
				}
			}
			/* tell the library how much we actually submitted */
			vorbis_analysis_wrote(&(pOgg->vd),i);
		}


		/* vorbis does some data preanalysis, then divvies up blocks for
			more involved (potentially parallel) processing.  Get a single
			block for encoding now */
		while(vorbis_analysis_blockout(&(pOgg->vd),&(pOgg->vb))==1){
			/* analysis, assume we want to use bitrate management */
			vorbis_analysis(&(pOgg->vb),NULL);
			vorbis_bitrate_addblock(&(pOgg->vb));

			while(vorbis_bitrate_flushpacket(&(pOgg->vd), &(pOgg->op))){
				/* weld the packet into the bitstream */
				ogg_stream_packetin(&(pOgg->os), &(pOgg->op));

				/* write out pages (if any) */
				while(!pOgg->eos){
					int result=ogg_stream_pageout(&(pOgg->os),&(pOgg->og));
					if(result==0) {
						break;
					}

					fwrite(pOgg->og.header,1,pOgg->og.header_len,pOgg->p_file_pout);
					fwrite(pOgg->og.body,1,pOgg->og.body_len,pOgg->p_file_pout);

					/* this could be set above, but for illustrative purposes, I do
						it here (to show that vorbis does know where the stream ends) */

					if(ogg_page_eos(&(pOgg->og))) {
//LOGI("break 2");
						pOgg->eos=1;
					}
				}
			}
		}


		p_buff += bytes;
		bytes_length -= bytes;
		if( bytes_length == 0 ) {
//			LOGI("break 3");
			break;
		}
//LOGI("bytes_length : %d", bytes_length);
	}
//LOGI("6");
}







signed char readbuffer[READ*4+44]; 



int ogg_encode_file(const char* fin_path,const char* fout_path)
{
  ogg_stream_state os; /* take physical pages, weld into a logical
                          stream of packets */
  ogg_page         og; /* one Ogg bitstream page.  Vorbis packets are inside */
  ogg_packet       op; /* one raw packet of data for decode */

  vorbis_info      vi; /* struct that stores all the static vorbis bitstream
                          settings */
  vorbis_comment   vc; /* struct that stores all the user comments */

  vorbis_dsp_state vd; /* central working state for the packet->PCM decoder */
  vorbis_block     vb; /* local working space for packet->PCM decode */

  int eos=0,ret;
  int i, founddata;
  int ch =1;
  FILE* fin, *fout;


#if defined(macintosh) && defined(__MWERKS__)
  int argc = 0;
  char **argv = NULL;
  argc = ccommand(&argv); /* get a "command line" from the Mac user */
                          /* this also lets the user set stdin and stdout */
#endif

  /* we cheat on the WAV header; we just bypass 44 bytes (simplest WAV
     header is 44 bytes) and assume that the data is 44.1khz, stereo, 16 bit
     little endian pcm samples. This is just an example, after all. */

#ifdef _WIN32 /* We need to set stdin/stdout to binary mode. Damn windows. */
  /* if we were reading/writing a file, it would also need to in
     binary mode, eg, fopen("file.wav","wb"); */
  /* Beware the evil ifdef. We avoid these where we can, but this one we
     cannot. Don't add any more, you'll probably go to hell if you do. */
  //_setmode( _fileno( stdin ), _O_BINARY );
  //_setmode( _fileno( stdout ), _O_BINARY );
#endif
  fin = fopen(fin_path, "rb");
  fout = fopen(fout_path, "wb");
  /*if(0 == freopen(fin, "rb", stdin))
	  printf("Cannot reopen stdin.");
  if(0 == freopen(fout, "wb", stdout))
      printf("Cannot reopen stdout.");
*/
  if(0 == fin || 0 == fout)
	  printf("Cannot open file.");  

  /* we cheat on the WAV header; we just bypass the header and never
     verify that it matches 16bit/stereo/44.1kHz.  This is just an
     example, after all. */

  readbuffer[0] = '\0';
  for (i=0, founddata=0; i<30 && ! feof(fin) && ! ferror(fin); i++)
  {
    fread(readbuffer,1,2,fin);

    if ( ! strncmp((char*)readbuffer, "da", 2) ){
      founddata = 1;
      fread(readbuffer,1,6,fin);
      break;
    }
  }

  /********** Encode setup ************/

  vorbis_info_init(&vi);

  /* choose an encoding mode.  A few possibilities commented out, one
     actually used: */

  /*********************************************************************
   Encoding using a VBR quality mode.  The usable range is -.1
   (lowest quality, smallest file) to 1. (highest quality, largest file).
   Example quality mode .4: 44kHz stereo coupled, roughly 128kbps VBR

   ret = vorbis_encode_init_vbr(&vi,2,44100,.4);

   ---------------------------------------------------------------------

   Encoding using an average bitrate mode (ABR).
   example: 44kHz stereo coupled, average 128kbps VBR

   ret = vorbis_encode_init(&vi,2,44100,-1,128000,-1);

   ---------------------------------------------------------------------

   Encode using a quality mode, but select that quality mode by asking for
   an approximate bitrate.  This is not ABR, it is true VBR, but selected
   using the bitrate interface, and then turning bitrate management off:

   ret = ( vorbis_encode_setup_managed(&vi,2,44100,-1,128000,-1) ||
           vorbis_encode_ctl(&vi,OV_ECTL_RATEMANAGE2_SET,NULL) ||
           vorbis_encode_setup_init(&vi));

   *********************************************************************/

  ret=vorbis_encode_init_vbr(&vi,ch,22050,0.1);

  /* do not continue if setup failed; this can happen if we ask for a
     mode that libVorbis does not support (eg, too low a bitrate, etc,
     will return 'OV_EIMPL') */

  if(ret)exit(1);

  /* add a comment */
  vorbis_comment_init(&vc);
  vorbis_comment_add_tag(&vc,"ENCODER","encoder_example.c");

  /* set up the analysis state and auxiliary encoding storage */
  vorbis_analysis_init(&vd,&vi);
  vorbis_block_init(&vd,&vb);

  /* set up our packet->stream encoder */
  /* pick a random serial number; that way we can more likely build
     chained streams just by concatenation */
  srand(time(NULL));
  ogg_stream_init(&os,rand());

  /* Vorbis streams begin with three headers; the initial header (with
     most of the codec setup parameters) which is mandated by the Ogg
     bitstream spec.  The second header holds any comment fields.  The
     third header holds the bitstream codebook.  We merely need to
     make the headers, then pass them to libvorbis one at a time;
     libvorbis handles the additional Ogg bitstream constraints */

  {
    ogg_packet header;
    ogg_packet header_comm;
    ogg_packet header_code;

    vorbis_analysis_headerout(&vd,&vc,&header,&header_comm,&header_code);
    ogg_stream_packetin(&os,&header); /* automatically placed in its own
                                         page */
    ogg_stream_packetin(&os,&header_comm);
    ogg_stream_packetin(&os,&header_code);

    /* This ensures the actual
     * audio data will start on a new page, as per spec
     */
    while(!eos){
      int result=ogg_stream_flush(&os,&og);
      if(result==0)break;
      fwrite(og.header,1,og.header_len,fout);
      fwrite(og.body,1,og.body_len,fout);
    }

  }

  while(!eos){
    long i;
    long bytes=fread(readbuffer,1,READ*ch*2,fin); /* stereo hardwired here */

    if(bytes==0){
      /* end of file.  this can be done implicitly in the mainline,
         but it's easier to see here in non-clever fashion.
         Tell the library we're at end of stream so that it can handle
         the last frame and mark end of stream in the output properly */
      vorbis_analysis_wrote(&vd,0);

    }else{
      /* data to encode */

      /* expose the buffer to submit data */
      float **buffer=vorbis_analysis_buffer(&vd,READ);

      /* uninterleave samples */
	  //two channels
	  if(ch==2)
	  {
		  for(i=0;i<bytes/4;i++){
			buffer[0][i]=((readbuffer[i*4+1]<<8)|
						  (0x00ff&(int)readbuffer[i*4]))/32768.f;
			buffer[1][i]=((readbuffer[i*4+3]<<8)|
						  (0x00ff&(int)readbuffer[i*4+2]))/32768.f;
		  }
	  }
	  else
	  {
		  //one channel
		  for(i=0;i<bytes/2;i++){
			buffer[0][i]=((readbuffer[i*2+1]<<8)|
						  (0x00ff&(int)readbuffer[i*2]))/32768.f;        
		  }
	  }
      /* tell the library how much we actually submitted */
      vorbis_analysis_wrote(&vd,i);
    }

    /* vorbis does some data preanalysis, then divvies up blocks for
       more involved (potentially parallel) processing.  Get a single
       block for encoding now */
    while(vorbis_analysis_blockout(&vd,&vb)==1){

      /* analysis, assume we want to use bitrate management */
      vorbis_analysis(&vb,NULL);
      vorbis_bitrate_addblock(&vb);

      while(vorbis_bitrate_flushpacket(&vd,&op)){

        /* weld the packet into the bitstream */
        ogg_stream_packetin(&os,&op);

        /* write out pages (if any) */
        while(!eos){
          int result=ogg_stream_pageout(&os,&og);
          if(result==0)break;
          fwrite(og.header,1,og.header_len,fout);
          fwrite(og.body,1,og.body_len,fout);

          /* this could be set above, but for illustrative purposes, I do
             it here (to show that vorbis does know where the stream ends) */

          if(ogg_page_eos(&og))eos=1;
        }
      }
    }
  }

  /* clean up and exit.  vorbis_info_clear() must be called last */

  ogg_stream_clear(&os);
  vorbis_block_clear(&vb);
  vorbis_dsp_clear(&vd);
  vorbis_comment_clear(&vc);
  vorbis_info_clear(&vi);
  fclose(fin);
  fclose(fout);

  /* ogg_page and ogg_packet structs always point to storage in
     libvorbis.  They're never freed or manipulated directly */

  fprintf(stderr,"Done.\n");
  return(0);
}
