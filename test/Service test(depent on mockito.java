import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// 测试Service层（使用Mockito模拟依赖）
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    
    @InjectMocks
    private GameService gameService;
    
    @Test
    void testFindGameById() {
        Game mockGame = new Game(1L, "Cyberpunk 2077");
        when(gameRepository.findById(1L)).thenReturn(Optional.of(mockGame));
        
        Game result = gameService.findGameById(1L);
        assertEquals("Cyberpunk 2077", result.getName());
    }
}