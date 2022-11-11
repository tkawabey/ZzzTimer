package four.non.bronds.yyys.zzztimer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageUtil {
	public static Bitmap	LoadImageFile(String strFile, long _nHeightDisp, long _nWidthDisp)
	{
		//画像読み込み
		Bitmap _bmpImageFile = BitmapFactory.decodeFile(strFile);
		
		int		nWidth = _bmpImageFile.getWidth();
		int		nHeight = _bmpImageFile.getHeight();
		//Log.d("Test119","ImageSize " + nWidth + "x" + nHeight);

		float	fScale;

		//拡大縮小率取得
		if(true)
		{
			//画像に合わせる
			if((long)nWidth * _nHeightDisp > (long)_nWidthDisp * nHeight)
				fScale = (float)_nWidthDisp / nWidth;
			else
				fScale = (float)_nHeightDisp / nHeight;
		}
		else if(false)
		{
			//高さに合わせる
			fScale = (float)_nHeightDisp / nHeight;
		}
		else
		{
			//幅に合わせる
			fScale = (float)_nWidthDisp / nWidth;
		}

		//リサイズ
		Matrix	matrix = new Matrix();
		matrix.postScale(fScale,fScale,0,0);
		Bitmap _bmpResized = Bitmap.createBitmap(_bmpImageFile,0,0,nWidth,nHeight,matrix,true);

//		if(bInvalidate)
//			invalidate();		//表示更新

		return	_bmpResized;
	}
}
