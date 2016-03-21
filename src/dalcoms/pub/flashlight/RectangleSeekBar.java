package dalcoms.pub.flashlight;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class RectangleSeekBar extends Rectangle {
	Font mTextFont;
	VertexBufferObjectManager vbom;
	Rectangle mTouchEffectRect;

	Text mTextTitle;
	HsMath hsMath;

	public RectangleSeekBar( float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager, Font pTextFont ) {
		super( pX, pY, pWidth, pHeight, pVertexBufferObjectManager );
		mTextFont = pTextFont;

		hsMath = new HsMath();
	}

}
