package com.xtu.stream_game.service;

import com.xtu.stream_game.entity.Developer;
import com.xtu.stream_game.repository.DeveloperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    @Test
    void getDeveloperById_ShouldReturnDeveloper() {
        Developer dev = new Developer();
        dev.setDeveloperId(1);
        when(developerRepository.findById(1)).thenReturn(Optional.of(dev));

        Developer result = developerService.getDeveloperById(1);
        assertEquals(1, result.getDeveloperId());
    }

    @Test
    void deleteDeveloper_ShouldInvokeRepositoryDelete() {
        doNothing().when(developerRepository).deleteById(1);
        developerService.deleteDeveloper(1);
        verify(developerRepository, times(1)).deleteById(1);
    }
}