package dalcoms.pub.flashlight;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.LayoutGameActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import dalcoms.pub.flashlight.scene.SceneManager;

@SuppressWarnings( "deprecation" )
public class MainActivity extends LayoutGameActivity {
	public Camera mCamera;

	private final int MYFPS = 60;
	private final float TIME_SPLASH2START = 2.4f;
	private boolean hasBeenDestroyedPaused = false;
	private boolean sceneMenuCreated = false;

	private AdView adMobAdView;

	//	private boolean isNotiServiceCreated = false;

	static android.hardware.Camera mHardwareCamera = null;
	private boolean mIsCameraFlashAvailable = false;
	Parameters mHardwareCameraParameter;

	private Point getResizedCameraSize( ) {
		Point retSize = new Point( 480, 789 );
		final int[] cameraRefHeight = { 1750, 1170, 780 };
		final Point[] useCameraSize = {
				new Point( 1080, 1776 ),
				new Point( 720, 1184 ),
				new Point( 480, 789 )
		};
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );

		Log.v("cameraSize",String.valueOf( displayMetrics.heightPixels ));
		for ( int i = 0 ; i < cameraRefHeight.length ; i++ ) {
			if ( displayMetrics.heightPixels > cameraRefHeight[i] ) {
				retSize = useCameraSize[i];
				break;
			}
		}

		//		retSize = new Point(480,789);

		return retSize;
	}

	@Override
	protected void onSetContentView( ) {
		super.onSetContentView();
		mRenderSurfaceView = new RenderSurfaceView( this );
		mRenderSurfaceView.setEGLConfigChooser( 8, 8, 8, 8, 24, 0 );
		mRenderSurfaceView.setRenderer( mEngine, this );
		mRenderSurfaceView.getHolder().setFormat( PixelFormat.RGBA_8888 );

		initAdmobAdView();

	}

	@Override
	public EngineOptions onCreateEngineOptions( ) {
		Point cameraSize = new Point();

		cameraSize = getResizedCameraSize();

		mCamera = new Camera( 0, 0, cameraSize.x, cameraSize.y );

		EngineOptions engineOptions = new EngineOptions(
				true,
				ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy( cameraSize.x, cameraSize.y ),
				mCamera );

		engineOptions.getRenderOptions().setDithering( true );
		engineOptions.getAudioOptions().setNeedsMusic( true ).setNeedsSound( true );
		engineOptions.setWakeLockOptions( WakeLockOptions.SCREEN_DIM );

		return engineOptions;
	}

	@Override
	public Engine onCreateEngine( EngineOptions pEngineOptions ) {

		return new LimitedFPSEngine( pEngineOptions, MYFPS );
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback )
			throws Exception {
		ResourcesManager.prepare( mEngine, this, mCamera, getVertexBufferObjectManager() );
		ResourcesManager.getInstance().loadResources();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene( OnCreateSceneCallback pOnCreateSceneCallback )
			throws Exception {
		SceneManager.getInstance().createSceneHome( pOnCreateSceneCallback );

	}

	@Override
	public void onPopulateScene( Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback ) throws Exception {

		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}

	@Override
	protected int getLayoutID( ) {
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID( ) {
		return R.id.gameSurfaceView;
	}

	@Override
	public void onDestroy( ) {
		ResourcesManager.getInstance().getVibrator().cancel();
		destroyHardwareCamera();
		hasBeenDestroyedPaused = true;
		adMobAdView.destroy();
		super.onDestroy();
		System.exit( 0 );
	}

	@Override
	public void onPause( ) {
		//		ResourcesManager.getInstance().mVibrator.cancel();
		try {
			ResourcesManager.getInstance().getVibrator().cancel();
			//			ResourcesManager.getInstance().getHardwareCamera().release();
			//			ResourcesManager.getInstance().releaseHardwareCamera();
			releaseHardwareCamera();
		} catch ( NullPointerException e ) {
			e.printStackTrace();
		}

		hasBeenDestroyedPaused = true;
		adMobAdView.pause();
		super.onPause();
	}

	@Override
	public synchronized void onResume( ) {
		adMobAdView.resume();
		//		ResourcesManager.getInstance().resumeHardwareCamera();
		resumeHardwareCamera();

		super.onResume();
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false;
	}

	@Override
	protected void onCreate( Bundle pSavedInstanceState ) {
		startNotiService();
		if ( !checkCameraFlashAvailable() ) {
			ResourcesManager.getInstance()
					.safeToastMessageShow( this.getString( R.string.no_camera ), Toast.LENGTH_SHORT );
		}

		super.onCreate( pSavedInstanceState );
	}

	private void startNotiService( ) {
		DalcomsAppList appList = new DalcomsAppList( this );
		if ( !appList.getUninstalledApps().isEmpty() ) {
			String pkgName = appList.getUninstalledApps().get( 0 );
			Intent intent = new Intent( this, NotiService.class );
			intent.putExtra( DalcomsAppList.EXTRA_APP_PACKAGE, pkgName );
			intent.putExtra( DalcomsAppList.EXTRA_APP_TITLE, appList.getPackageTitle( pkgName ) );
			intent.putExtra( DalcomsAppList.EXTRA_APP_DESCRIPTION, appList.getPackageDescription( pkgName ) );

			try {
				startService( intent );
			} catch ( Exception e ) {
				e.printStackTrace();
			}

		}

	}

	private void initAdmobAdView( ) {
		adMobAdView = ( AdView ) this.findViewById( R.id.adView );

		AdRequest request = new AdRequest.Builder().
				addTestDevice( AdRequest.DEVICE_ID_EMULATOR ).
				build();

		adMobAdView.loadAd( request );
		adMobAdView.setBackgroundColor( android.graphics.Color.TRANSPARENT );

	}

	private boolean checkCameraFlashAvailable( ) {
		return mIsCameraFlashAvailable = getApplicationContext().getPackageManager()
				.hasSystemFeature( PackageManager.FEATURE_CAMERA_FLASH );
	}

	private void setHardwareCamera( ) {
		try {
			mHardwareCamera = android.hardware.Camera.open( CameraInfo.CAMERA_FACING_BACK );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		mHardwareCameraParameter = mHardwareCamera.getParameters();
		mHardwareCameraParameter.setFlashMode( Parameters.FLASH_MODE_OFF );
		mHardwareCamera.setParameters( mHardwareCameraParameter );
		mHardwareCamera.startPreview();
	}

	private class SetHardwareCameraTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground( Void... params ) {
			Log.v("cameraFail",String.valueOf( android.hardware.Camera.getNumberOfCameras() ));
			try {
				mHardwareCamera = android.hardware.Camera.open( CameraInfo.CAMERA_FACING_BACK );
			} catch ( Exception e ) {
				e.printStackTrace();
				android.hardware.Camera.open().unlock();
				mHardwareCamera = android.hardware.Camera.open( CameraInfo.CAMERA_FACING_BACK );
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void result ) {
			mHardwareCameraParameter = mHardwareCamera.getParameters();
			mHardwareCameraParameter.setFlashMode( Parameters.FLASH_MODE_OFF );
			mHardwareCamera.setParameters( mHardwareCameraParameter );
			mHardwareCamera.startPreview();
		}

	}

	public android.hardware.Camera getHardwareCamera( ) {
		return this.mHardwareCamera;
	}

	public void releaseHardwareCamera( ) {
		this.mHardwareCamera.release();
		this.mHardwareCamera = null;
	}

	public boolean isCameraFlashAvailable( ) {
		return this.mIsCameraFlashAvailable;
	}

	public void destroyHardwareCamera( ) {
		if ( mHardwareCamera != null ) {
			mHardwareCamera.stopPreview();
			mHardwareCamera.setPreviewCallback( null );
			mHardwareCamera = null;
		}
	}

	public void resumeHardwareCamera( ) {
		//		setHardwareCamera();
		new SetHardwareCameraTask().execute();
	}

	public void turnOnOffCameraFlash( boolean pOnOff ) {
		try {
			if ( pOnOff ) {
				mHardwareCameraParameter.setFlashMode( Parameters.FLASH_MODE_TORCH );
			} else {
				mHardwareCameraParameter.setFlashMode( Parameters.FLASH_MODE_OFF );
			}
			mHardwareCamera.setParameters( mHardwareCameraParameter );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}