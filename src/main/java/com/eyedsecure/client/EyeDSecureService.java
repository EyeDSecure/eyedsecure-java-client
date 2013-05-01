package com.eyedsecure.client;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Send parallel server requests
 */
public class EyeDSecureService {
	private ExecutorCompletionService<Response> completionService;

	/**
	 * Sets up thread pool for requests.
	 */
	public EyeDSecureService() {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(0, 50, 300L,
				TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
	    completionService = new ExecutorCompletionService<Response>(pool);
	}

	public Response fetch(List<String> urls, String userAgent) throws RequestException {
	    List<Future<Response>> tasks = new ArrayList<Future<Response>>();
	    for(String url : urls) {
	    	tasks.add(completionService.submit(new ServerTask(url, userAgent)));
	    }
	    Response response = null;
		try {
			int tasksDone = 0;
			Throwable savedException = null;
			Future<Response> futureResponse = completionService.poll(1L, TimeUnit.MINUTES);
			while(futureResponse != null) {
				try {
					tasksDone++;
					tasks.remove(futureResponse);
					response = futureResponse.get();

                    // If response code is  REPLAYED_REQUEST, server may have received request
                    // after synchronization on the backend. Keep checking for at least
                    // one positive response.
					if(!response.getResponseCode().equals(ResponseCode.REPLAYED_REQUEST)) {
						break;
					}
				} catch (CancellationException ignored) {
					tasksDone--;
				} catch (ExecutionException e) {
					savedException = e.getCause();
				}
				if(tasksDone >= urls.size()) {
					break;
				}
				futureResponse = completionService.poll(1L, TimeUnit.MINUTES);
			}
			if(futureResponse == null || response == null) {
				if(savedException != null) {
					throw new RequestException(
							"Exception while executing request.", savedException);
				} else {
					throw new RequestException("Request timeout.");
				}
			}
		} catch (InterruptedException e) {
			throw new RequestException("Request interrupted.", e);
		}

		for(Future<Response> task : tasks) {
			task.cancel(true);
		}

	    return response;
	}

	class ServerTask implements Callable<Response> {
		private final String url;
		private final String userAgent;


		/**
		 * Set up a ServerTask
		 * @param url the url to be used
		 * @param userAgent sent to the server, or NULL to use default
		 */
		public ServerTask(String url, String userAgent) {
			this.url = url;
			this.userAgent = userAgent;
		}

		/**
		 * Do the server query for given URL.
		 * @throws Exception
		 */
		public Response call() throws Exception {
			URL url = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if(userAgent == null) {
				conn.setRequestProperty("User-Agent", "eyedsecure-java-client/" + Version.version());
			} else {
				conn.setRequestProperty("User-Agent", userAgent);
			}
			conn.setConnectTimeout(20000); // 20 second timeout
			conn.setReadTimeout(20000); // 20 second timeout for both read and connect
			return new ResponseParser(conn.getInputStream()).parse();
		}	
	}
}
