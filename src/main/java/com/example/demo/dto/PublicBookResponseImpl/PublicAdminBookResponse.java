package  com.example.demo.dto.PublicBookResponseImpl;

import com.example.demo.dto.PublicBookResponse;
import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain=true)
public class PublicAdminBookResponse implements PublicBookResponse{
    private int id;
    private String title;
    private String author;
    private int stock;
    private String librarianUsername;
}