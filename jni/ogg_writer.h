#ifndef __OGG_WRITER_H__
#define __OGG_WRITER_H__

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <vorbis/vorbisenc.h>

typedef struct _OggInfoTag
{
	char*	p_name;
	char*	p_value;
}OggInfoTag;


typedef struct _OggInfo
{
	int				 ch; /* Channel */
	ogg_stream_state os; /* take physical pages, weld into a logical
						  stream of packets */
	ogg_page         og; /* one Ogg bitstream page.  Vorbis packets are inside */
	ogg_packet       op; /* one raw packet of data for decode */

	vorbis_info      vi; /* struct that stores all the static vorbis bitstream
						  settings */
	vorbis_comment   vc; /* struct that stores all the user comments */

	vorbis_dsp_state vd; /* central working state for the packet->PCM decoder */
	vorbis_block     vb; /* local working space for packet->PCM decode */

	FILE*			  p_file_pout;

	int				eos;
}OggInfo;

OggInfo*	ogg_init(int ch, const char* p_out, OggInfoTag* p_tags, int tag_cnt);
void		ogg_close(OggInfo* pOgg);
void        ogg_write(OggInfo* pOgg, signed char* p_buff, long bytes);
int ogg_encode_file(const char* fin_path,const char* fout_path);


#endif /*__OGG_WRITER_H__*/
