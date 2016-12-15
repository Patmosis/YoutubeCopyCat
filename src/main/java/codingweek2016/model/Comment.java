package codingweek2016.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class Comment extends JPanel {
	
    private static YouTube youtube;
    
	private String videoId;
    
    public Comment (String id){
    	videoId = id;
    }
	
	public List<CommentThread> getComments() throws IOException {
		
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		 
        try {
            // Authorize the request.
            Credential credential = Authentification.authorize(scopes, "commentthreads");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Authentification.HTTP_TRANSPORT, Authentification.JSON_FACTORY, credential).setApplicationName("youtube-cmdline-commentthreads-sample").build();
            
           // YouTube.Search.List search = youtube.search().list("id,snippet");

			//YouTube.CommentThreads.List request = youtube.commentThreads().list("");
			CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads().list("snippet").setVideoId(videoId).setTextFormat("plainText").execute();
		    List<CommentThread> videoComments = videoCommentsListResponse.getItems();
            
		    return videoComments;
		    
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode()
                    + " : " + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
		return null;
	}
	
	public void postcomment(String text){
		CommentSnippet commentSnippet = new CommentSnippet();
        commentSnippet.setTextOriginal(text);
        
        // Create a top-level comment with snippet.
        com.google.api.services.youtube.model.Comment topLevelComment = new com.google.api.services.youtube.model.Comment();
        topLevelComment.setSnippet(commentSnippet);
        
        CommentThreadSnippet commentThreadSnippet = new CommentThreadSnippet();
		commentThreadSnippet.setVideoId(videoId);
		commentThreadSnippet.setTopLevelComment(topLevelComment);
		
		CommentThread commentThread = new CommentThread();
        commentThread.setSnippet(commentThreadSnippet);
        
        // Call the YouTube Data API's commentThreads.insert method to
        // create a comment.
        try {
			CommentThread videoCommentInsertResponse = youtube.commentThreads().insert("snippet", commentThread).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JPanel display( List<CommentThread> videoComments){
		
		JPanel commentlist = new JPanel();
		commentlist.setLayout(new BoxLayout(commentlist, BoxLayout.Y_AXIS));
		
		for (CommentThread videoComment : videoComments) {
			JPanel comment = new JPanel();
			comment.setLayout(new BorderLayout());
			comment.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();

			final JButton author = new JButton(snippet.getAuthorDisplayName());
			author.setPreferredSize(new Dimension(200, 100));
			author.setText("<html><body><u>"+author.getText()+"</u></body><html/>");
			try {
				ImageIcon img = new ImageIcon(ImageIO.read(new File("src/main/resources/icon.png")));
				author.setIcon(new ImageIcon(img.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
			} catch (IOException e) {
				e.printStackTrace();
			}	
			author.setOpaque(false);
			author.setContentAreaFilled(false);
			author.setBorderPainted(false);
			author.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent arg0) {
					// Do nothing
				}

				public void mouseEntered(MouseEvent arg0) {
					author.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				public void mouseExited(MouseEvent arg0) {
					author.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				public void mousePressed(MouseEvent arg0) {
					// Do nothing
				}

				public void mouseReleased(MouseEvent arg0) {
					// Do nothing
				}
	        });
			author.addActionListener(new ActionListener() {
				  
	            public void actionPerformed(ActionEvent e) {
	            	System.out.println("author");
	            }
	        });
			
			JLabel text = new JLabel(snippet.getTextDisplay());
			
			comment.add(author, BorderLayout.NORTH);
			comment.add(text, BorderLayout.CENTER);
			
			commentlist.add(comment);
		}
		return commentlist;	
	}
}