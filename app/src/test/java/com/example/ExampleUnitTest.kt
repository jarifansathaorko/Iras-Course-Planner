package com.example

import com.example.data.GitHubUpdateManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testGitHubVersionComparison() {
    // Newer versions
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.0.1", "1.0"))
    assertTrue(GitHubUpdateManager.isNewerVersion("1.0.2", "1.0.1"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v2.0.0", "1.9.5"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.1", "1.0.9"))
    assertTrue(GitHubUpdateManager.isNewerVersion("release-1.0.3", "1.0.2"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.0.3-beta", "1.0.2"))
    assertTrue(GitHubUpdateManager.isNewerVersion("v1.0.3+build42", "1.0.2"))

    // Same or older versions
    assertFalse(GitHubUpdateManager.isNewerVersion("v1.0", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("1.0.0", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("v0.9.9", "1.0"))
    assertFalse(GitHubUpdateManager.isNewerVersion("v1.0.1", "1.0.2"))
    assertFalse(GitHubUpdateManager.isNewerVersion("1.0.2", "1.0.2"))
    assertFalse(GitHubUpdateManager.isNewerVersion("v1.0.2", "1.0.2"))
    assertFalse(GitHubUpdateManager.isNewerVersion("release-1.0.2", "1.0.2"))
  }

  @Test
  fun testCleanVersion() {
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("v1.0.2"))
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("V1.0.2"))
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("release-1.0.2"))
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("Release-1.0.2"))
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("v1.0.2-beta.1"))
    assertEquals("1.0.2", GitHubUpdateManager.cleanVersion("v1.0.2+hotfix"))
  }
}
