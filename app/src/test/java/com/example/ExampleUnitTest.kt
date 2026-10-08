package com.example

import com.example.data.GitHubUpdateManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGitHubVersionComparison() {
    // Newer versions
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.0.1", "1.0"))
    assertTrue(GitHubUpdateManager.isNewerVersion("1.0.2", "1.0.1"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v2.0.0", "1.9.5"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.1", "1.0.9"))

    // Same or older versions
    assertFalse(GitHubUpdateManager.isNewerVersion("v1.0", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("1.0.0", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("v0.9.9", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("v1.0.1", "1.0.2"))
  }
}
