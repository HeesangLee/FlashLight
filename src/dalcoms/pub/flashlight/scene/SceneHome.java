package dalcoms.pub.flashlight.scene;

import java.util.Random;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBackOut;

import android.app.Activity;
import android.widget.Toast;
import dalcoms.pub.flashlight.GoMarketSharStarAnimatedSprite;
import dalcoms.pub.flashlight.Gotype;
import dalcoms.pub.flashlight.R;
import dalcoms.pub.flashlight.RectangleOnOffButton;
import dalcoms.pub.flashlight.RectangleSeekBar;
import dalcoms.pub.flashlight.ResourcesManager;

public class SceneHome extends BaseScene
		implements IOnSceneTouchListener {
	final String TAG = this.getClass().getSimpleName();
	HsMath hsMath = new HsMath();
	boolean flag_interstitialAdOn = false;
	private final float mCameraWidth = camera.getWidth();
	final boolean INITIAL_BTN_STATUS = false;
	TiledSprite mLightOnEffectSprite;
	private boolean LIGHT_ON_OFF = false;

	RectangleSeekBar RectSeekBarOnTime;
	RectangleSeekBar RectSeekBarOffTime;
	AnimatedSprite aSpriteMarket, aSpriteShare, aSpriteStar;

	FlashOnOffInterval mFlashOnOffInterval;

	final float SCENE_TIMER_TIME = 0.05f;
	final float INIT_ON_RATIO = 1f;
	final float INIT_OFF_RATIO = 0f;

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );

		mFlashOnOffInterval = new FlashOnOffInterval( INIT_ON_RATIO, INIT_OFF_RATIO );
		setOnSceneTouchListener( this );

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );

		this.engine.registerUpdateHandler( new TimerHandler( SCENE_TIMER_TIME, true, new ITimerCallback() {

			@Override
			public void onTimePassed( TimerHandler pTimerHandler ) {
				flashLightOnControlcheckTimer();
			}
		} ) );

	}

	private void flashLightOnControlcheckTimer( ) {
		resourcesManager.turnOnOffCameraFlash( isLightOn()&mFlashOnOffInterval.isLightOn() );
		setLightOnOffEffect( isLightOn(), mFlashOnOffInterval.isLightOn() );// set blink via blink status;
		mFlashOnOffInterval.next();
	}

	@Override
	public void attachSprites( ) {
		this.attachMarketShareStarAnimatedSprites();
		this.attachLightOnEffect( INITIAL_BTN_STATUS );
		this.attachTitileText();
		this.attachCompanyText();
		this.attachOnOffButton( INITIAL_BTN_STATUS );
		this.attachOnOffSeekBars( INITIAL_BTN_STATUS );
	}

	private void attachOnOffSeekBars( boolean pInitialBtnStatus ) {
		final float pWidth = resourcesManager.applyResizeFactor( 800f );
		final float pHeight = resourcesManager.applyResizeFactor( 100f );
		final float pX = hsMath.getAlignCenterFloat( pWidth, camera.getWidth() );
		final float pYOn = resourcesManager.applyResizeFactor( 910f );
		final float pYOff = resourcesManager.applyResizeFactor( 1138f );

		RectSeekBarOnTime = new RectangleSeekBar( pX, pYOn, pWidth, pHeight, vbom,
				resourcesManager.getFontButton(),
				"ON",
				appColor.SEEK_BAR,
				appColor.SEEK_BAR_ACTIVEBAR_EN,
				appColor.SEEK_BAR_ACTIVEBAR_DIS,
				appColor.SEEK_BAR_SW_EN,
				appColor.SEEK_BAR_SW_DIS,
				INIT_ON_RATIO,
				pInitialBtnStatus );

		attachChild( RectSeekBarOnTime );
		registerTouchArea( RectSeekBarOnTime );

		RectSeekBarOffTime = new RectangleSeekBar( pX, pYOff, pWidth, pHeight, vbom,
				resourcesManager.getFontButton(),
				"OFF",
				appColor.SEEK_BAR,
				appColor.SEEK_BAR_ACTIVEBAR_EN,
				appColor.SEEK_BAR_ACTIVEBAR_DIS,
				appColor.SEEK_BAR_SW_EN,
				appColor.SEEK_BAR_SW_DIS,
				INIT_OFF_RATIO,
				pInitialBtnStatus );

		attachChild( RectSeekBarOffTime );
		registerTouchArea( RectSeekBarOffTime );

	}

	private void setEnableOnOffSeekBars( boolean pOnSeekBarEn, boolean pOffSeekBarEn ) {
		RectSeekBarOnTime.setEnable( pOnSeekBarEn );
		RectSeekBarOffTime.setEnable( pOffSeekBarEn );
	}

	private void attachLightOnEffect( boolean pInitialBtnStatus ) {
		mLightOnEffectSprite = new TiledSprite( 0, 0, resourcesManager.regionLightOnEffect, vbom );
		mLightOnEffectSprite.setX( hsMath.getAlignCenterFloat( mLightOnEffectSprite.getWidth(),
				camera.getWidth() ) );
		attachChild( mLightOnEffectSprite );

		setLightOnOffEffect( INITIAL_BTN_STATUS, false );
	}

	private void setLightOnOffEffect( boolean pOnOff, boolean pBlinkOnOff ) {
		this.mLightOnEffectSprite.setVisible( pOnOff );
		this.mLightOnEffectSprite.setCurrentTileIndex( ( pBlinkOnOff & pOnOff ) ? 1 : 0 );
	}

	private void attachOnOffButton( boolean pInitialBtnStatus ) {
		RectangleOnOffButton pOnOffButton = new RectangleOnOffButton( 0, 0,
				resourcesManager.applyResizeFactor( 800f ), resourcesManager.applyResizeFactor( 300f ),
				vbom, resourcesManager.regionOnOffIcon, resourcesManager.getFontButton(), pInitialBtnStatus ) {

			@Override
			public void isButtonToggled( ) {
				super.isButtonToggled();
				setButtonOnOff( isButtonOn() );
			}
		};
		pOnOffButton.setColor( appColor.ONOFF_BUTTON );
		pOnOffButton.setCenterPosition( camera.getCenterX(),
				resourcesManager.applyResizeFactor( 543.768f ) );
		attachChild( pOnOffButton );
		registerTouchArea( pOnOffButton );

	}

	private void setButtonOnOff( boolean pBtnOnOff ) {
		if ( resourcesManager.isCameraFlashAvailable() ) {
			this.LIGHT_ON_OFF = pBtnOnOff;
			//			setLightOnOffEffect( isLightOn(), true );// set blink via blink status;
			setEnableOnOffSeekBars( isLightOn(), isLightOn() );
			//			resourcesManager.turnOnOffCameraFlash( isLightOn() );
		} else {
			resourcesManager.safeToastMessageShow( activity.getString( R.string.no_camera ),
					Toast.LENGTH_SHORT );
		}
	}

	private boolean isLightOn( ) {
		return this.LIGHT_ON_OFF;
	}

	private void attachTitileText( ) {
		final float pY = resourcesManager.applyResizeFactor( 170f );
		Text pTitleText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.app_title ), vbom );
		pTitleText.setPosition( appComm.getAlignCenterFloat( pTitleText.getWidth(), camera.getWidth() ), pY );
		attachChild( pTitleText );
		pTitleText.setColor( appColor.FONT_DEFAULT );

		pTitleText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachCompanyText( ) {
		final float pY = resourcesManager.applyResizeFactor( 1630f );
		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.company_name ), vbom );
		pText.setPosition( appComm.getAlignCenterFloat( pText.getWidth(), camera.getWidth() ), pY );
		attachChild( pText );
		pText.setColor( appColor.FONT_DEFAULT );
		pText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachMarketShareStarAnimatedSprites( ) {
		//		final float pY = resourcesManager.applyResizeFactor( 1420f );
		final float pY = camera.getHeight();
		float[] pX = appComm.getDistributedCenterOrgPosition(
				resourcesManager.regionMarketShareStar.getWidth(), 3,
				resourcesManager.applyResizeFactor( 640f ),
				( camera.getWidth() - resourcesManager.applyResizeFactor( 640f ) ) / 2f );

		AnimatedSprite aSpriteMarket = new GoMarketSharStarAnimatedSprite( pX[0], pY,
				resourcesManager.regionMarketShareStar, vbom ).activityOn( activity ).goType(
				Gotype.GO_MARKET );

		aSpriteMarket.animate( new long[] { 500, 500 }, 0, 1, true );

		AnimatedSprite aSpriteShare = new GoMarketSharStarAnimatedSprite( pX[1], pY,
				resourcesManager.regionMarketShareStar, vbom )
				.activityOn( activity )
				.goType( Gotype.GO_SHARE )
				.shareInformation( activity.getResources().getString( R.string.share_subject ),
						activity.getString( R.string.share_text ), activity.getString( R.string.app_id ) );

		aSpriteShare.animate( new long[] { 500, 500 }, 2, 3, true );

		AnimatedSprite aSpriteStar = new GoMarketSharStarAnimatedSprite( pX[2], pY,
				resourcesManager.regionMarketShareStar, vbom ).activityOn( activity ).goType( Gotype.GO_STAR )
				.appId( activity.getString( R.string.app_id ) );

		aSpriteStar.animate( new long[] { 500, 500 }, 4, 5, true );

		attachChild( aSpriteMarket );
		registerTouchArea( aSpriteMarket );
		attachChild( aSpriteStar );
		registerTouchArea( aSpriteStar );
		attachChild( aSpriteShare );
		registerTouchArea( aSpriteShare );

		//		aSpriteMarket.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		//		aSpriteStar.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		//		aSpriteShare.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
	}

	@Override
	public Engine getEngine( ) {
		return this.engine;
	}

	@Override
	public Activity getActivity( ) {
		return this.activity;
	}

	@Override
	public VertexBufferObjectManager getVbom( ) {
		return this.vbom;
	}

	@Override
	public Camera getCamera( ) {
		return this.camera;
	}

	@Override
	public ResourcesManager getResourcesManager( ) {
		return this.resourcesManager;
	}

	@Override
	public SceneManager getSceneManager( ) {
		return this.sceneManager;
	}

	@Override
	public void onBackKeyPressed( ) {
		Random rand = new Random();
		if ( rand.nextInt( 20 ) < 4 ) {
			if ( flag_interstitialAdOn == false ) {
				flag_interstitialAdOn = true;
				sceneManager.popAdmobInterstitialAd();
			}
		}
		appComm.backKeyPressed( 0.85f );
	}

	@Override
	public SceneType getSceneType( ) {
		return SceneType.SCENE_HOME;
	}

	@Override
	public void disposeScene( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSceneTouchEvent( Scene pScene, TouchEvent pSceneTouchEvent ) {
		if ( pSceneTouchEvent.isActionUp() ) {
			if ( ( RectSeekBarOnTime != null ) && ( RectSeekBarOffTime != null ) ) {
				mFlashOnOffInterval.resetCurrentIndex();
				mFlashOnOffInterval.setOnInterval( RectSeekBarOnTime.getSeekRatio() );
				mFlashOnOffInterval.setOffInterval( RectSeekBarOffTime.getSeekRatio() );
			}
		}
		return false;
	}

	private class FlashOnOffInterval {
		final float MAX_INTERVAL = 60;
		private int onInterval = 0;
		private int offInterval = 0;
		private int onOffInterval = 0;
		private int curIndex = 0;

		public FlashOnOffInterval( float pOnIntervalRatio, float pOffIntervalRatio ) {
			setOnInterval( pOnIntervalRatio );
			setOffInterval( pOffIntervalRatio );
		}

		private void setOnInterval( float pOnIntervalRatio ) {
			this.onInterval = Math.round( MAX_INTERVAL * pOnIntervalRatio );
			setOnOffInterval( getOnInterval(), getOffInterval() );
		}

		public int getOnInterval( ) {
			return this.onInterval;
		}

		private void setOffInterval( float pOffIntervalRatio ) {
			this.offInterval = Math.round( MAX_INTERVAL * pOffIntervalRatio );
			setOnOffInterval( getOnInterval(), getOffInterval() );
		}

		public int getOffInterval( ) {
			return this.offInterval;
		}

		public void resetCurrentIndex( ) {
			this.curIndex = 0;
		}

		public int getCurrentIndex( ) {
			return this.curIndex;
		}

		private void setOnOffInterval( int pOnInterval, int pOffInterval ) {
			onOffInterval = pOnInterval + pOffInterval;
		}

		public int getOnOffInterval( ) {
			return onOffInterval;
		}

		public boolean isLightOn( ) {
			boolean result = false;

			if ( getCurrentIndex() < getOnInterval() ) {
				result = true;
			}
			return result;
		}

		public boolean next( ) {//true = light on , false = light off
			this.curIndex = this.curIndex < getOnOffInterval() ? this.curIndex + 1 : 0;

			return this.isLightOn();
		}
	}

}