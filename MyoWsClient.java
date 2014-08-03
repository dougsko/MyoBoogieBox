import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;

public class MyoWsClient extends WebSocketClient {
	MIDIplayer mplayer;
	Gson gson;
	
	
	public MyoWsClient( URI serverUri, MIDIplayer mplayer ) {
		super( serverUri);
		this.mplayer = mplayer;
		gson = new Gson();
	}

	public void onOpen( ServerHandshake handshakedata ) {
		System.out.println( "opened connection" );
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	public void onMessage( String message ) {
		//System.out.println( "received: " + message );
		MyoEvent me = gson.fromJson(message, MyoEvent.class);
		mplayer.processMyoEvent(me);
		//System.out.println(me.frame.pose);
	}

	public void onFragment( Framedata fragment ) {
		System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );
	}

	public void onClose( int code, String reason, boolean remote ) {
		// The codecodes are documented in class org.java_websocket.framing.CloseFrame
		System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
	}

	public void onError( Exception ex ) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}
	/*
	public static void main( String[] args ) throws URISyntaxException {
		ExampleClient c = new ExampleClient( new URI( "ws://localhost:8887" ), new Draft_10() ); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
		c.connect();
	}
	*/

}