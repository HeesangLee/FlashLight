package dalcoms.pub.flashlight;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class RectangleOnOffButton extends Rectangle {

	ITiledTextureRegion mITiledRegionOnOff;
	Font mTextFont;
	boolean mButtonOnOffStatus;

	public RectangleOnOffButton( float pX, float pY,
			float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager,
			ITiledTextureRegion pITiledRegionOnOff,
			Font pTextFont,
			boolean pButtonOnOffStatus ) {

		super( pX, pY, pWidth, pHeight, pVertexBufferObjectManager );

		mITiledRegionOnOff = pITiledRegionOnOff;
		mTextFont = pTextFont;
		mButtonOnOffStatus = pButtonOnOffStatus;

		ResourcesManager.getInstance().getEngine().runOnUpdateThread( new Runnable() {

			@Override
			public void run( ) {
				attachInnerComponents( mButtonOnOffStatus );
			}
		} );
	}

	private void attachInnerComponents( boolean pButtonStatus ) {

	}

	private void attachOnOffIcon( boolean pButtonStatus ) {

	}

	private void attachOnOffText( boolean pButtonStatus ) {

	}

	public boolean isButtonOn( ) {
		return this.mButtonOnOffStatus;
	}

	@Override
	public boolean onAreaTouched( TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY ) {
		if ( pSceneTouchEvent.isActionDown() ) {
		} else {
			if ( pSceneTouchEvent.isActionUp() ) {

				isButtonToggled();
			}
		}

		return super.onAreaTouched( pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY );
	}

	public abstract void isButtonToggled( );

}
