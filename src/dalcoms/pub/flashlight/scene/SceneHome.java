package dalcoms.pub.flashlight.scene;

import java.util.Random;

import lib.dalcoms.andengineheesanglib.utils.HsMath;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBackOut;

import android.app.Activity;
import dalcoms.pub.flashlight.GoMarketSharStarAnimatedSprite;
import dalcoms.pub.flashlight.Gotype;
import dalcoms.pub.flashlight.R;
import dalcoms.pub.flashlight.RectangleOnOffButton;
import dalcoms.pub.flashlight.ResourcesManager;

public class SceneHome extends BaseScene {
	final String TAG = this.getClass().getSimpleName();

	HsMath hsMath = new HsMath();

	boolean flag_interstitialAdOn = false;

	private final float mCameraWidth = camera.getWidth();

	@Override
	public void createScene( ) {
		this.setBackground( new Background( this.appColor.APP_BACKGROUND ) );

		this.engine.runOnUpdateThread( new Runnable() {
			@Override
			public void run( ) {
				attachSprites();
			}
		} );
	}

	@Override
	public void attachSprites( ) {
		this.attachMarketShareStarAnimatedSprites();
		this.attachTitileText();
		this.attachCompanyText();
		this.attachOnOffButton( false );
	}

	private void attachOnOffButton( boolean pInitialBtnStatus ) {
		RectangleOnOffButton pOnOffButton = new RectangleOnOffButton( 0, 0,
				resourcesManager.applyResizeFactor( 800f ), resourcesManager.applyResizeFactor( 300f ),
				vbom, resourcesManager.regionOnOffIcon, resourcesManager.getFontButton(), pInitialBtnStatus ) {

			@Override
			public void isButtonToggled( ) {
				// TODO Auto-generated method stub

			}
		};
		pOnOffButton.setColor( appColor.ONOFF_BUTTON );
		attachChild( pOnOffButton );
		registerTouchArea( pOnOffButton );

	}

	private void attachTitileText( ) {
		final float pY = resourcesManager.applyResizeFactor( 170f );
		Text pTitleText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.app_name ), vbom );
		pTitleText.setPosition( appComm.getAlignCenterFloat( pTitleText.getWidth(), camera.getWidth() ), pY );
		attachChild( pTitleText );
		pTitleText.setColor( appColor.FONT_DEFAULT );

		pTitleText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachCompanyText( ) {
		final float pY = resourcesManager.applyResizeFactor( 1500f );
		Text pText = new Text( 0, 0, resourcesManager.getFontDefault(),
				activity.getString( R.string.company_name ), vbom );
		pText.setPosition( appComm.getAlignCenterFloat( pText.getWidth(), camera.getWidth() ), pY );
		attachChild( pText );
		pText.setColor( appColor.FONT_DEFAULT );
		pText
				.registerEntityModifier( new ScaleModifier( 2.5f, 0.1f, 1f, 1f, 1f, EaseBackOut.getInstance() ) );
	}

	private void attachMarketShareStarAnimatedSprites( ) {
		final float pY = resourcesManager.applyResizeFactor( 1285f );
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

		aSpriteMarket.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		aSpriteStar.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
		aSpriteShare.registerEntityModifier( new ScaleModifier( 0.5f, 0f, 1f ) );
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
}