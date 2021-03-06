/*
 * Copyright 2011 France Telecom R&D Beijing Co., Ltd 北京法国电信研发中心有限公司
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.weibo.api.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.social.weibo.api.CursoredList;
import org.springframework.social.weibo.api.Favorite;
import org.springframework.social.weibo.api.Favorite.Tag;
import org.springframework.social.weibo.matcher.StatusMatcher;

public class FavoriteTemplateTest extends AbstractWeiboOperationsTest {

	private FavoriteTemplate favoriteTemplate;

	@Override
	public void setUp() {
		favoriteTemplate = new FavoriteTemplate(getObjectMapper(),
				getRestTemplate(), true);
	}

	@Test
	public void testCreateFavorite() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/create.json"))
				.andExpect(method(POST))
				.andExpect(content().string("id=1"))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("favorite"), MediaType.APPLICATION_JSON));
		verifyFavorite(favoriteTemplate.createFavorite(1));
	}

	@Test
	public void testDeleteFavorite() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/destroy.json"))
				.andExpect(method(POST))
				.andExpect(content().string("id=1"))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("favorite"), MediaType.APPLICATION_JSON));
		verifyFavorite(favoriteTemplate.deleteFavorite(1));
	}

	@Test
	public void testDeleteFavorites() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/destroy_batch.json"))
				.andExpect(method(POST)).andExpect(content().string("ids=1%2C2%2C3"))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(withSuccess("{\"result\":true}", MediaType.APPLICATION_JSON));
		assertTrue(favoriteTemplate.deleteFavorites(Arrays.asList(1L, 2L, 3L)));
	}

	@Test
	public void testGetFavorite() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/show.json?id=1"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("favorite"), MediaType.APPLICATION_JSON));
		verifyFavorite(favoriteTemplate.getFavorite(1));
	}

	@Test
	public void testGetFavorites() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites.json"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavorites"),
								MediaType.APPLICATION_JSON));
		CursoredList<Favorite> cursoredList = favoriteTemplate.getFavorites();
		assertEquals(16, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		Favorite firstFavorite = cursoredList.iterator().next();
		verifyFavorite(firstFavorite);
	}

	@Test
	public void testGetFavoritesByTag() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/by_tags.json?tid=1"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavorites"),
								MediaType.APPLICATION_JSON));
		CursoredList<Favorite> cursoredList = favoriteTemplate
				.getFavoritesByTag(1);
		assertEquals(16, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		Favorite firstFavorite = cursoredList.iterator().next();
		verifyFavorite(firstFavorite);
	}

	@Test
	public void testGetFavoritesByTagPagination() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/by_tags.json?tid=1&count=20&page=5"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavorites"),
								MediaType.APPLICATION_JSON));
		CursoredList<Favorite> cursoredList = favoriteTemplate
				.getFavoritesByTag(1, 20, 5);
		assertEquals(16, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		Favorite firstFavorite = cursoredList.iterator().next();
		verifyFavorite(firstFavorite);
	}

	@Test
	public void testGetFavoritesPagination() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites.json?count=20&page=5"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavorites"),
								MediaType.APPLICATION_JSON));
		CursoredList<Favorite> cursoredList = favoriteTemplate.getFavorites(20,
				5);
		assertEquals(16, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		Favorite firstFavorite = cursoredList.iterator().next();
		verifyFavorite(firstFavorite);
	}

	@Test
	public void testGetTags() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/tags.json"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavoriteTags"),
								MediaType.APPLICATION_JSON));
		CursoredList<Tag> cursoredList = favoriteTemplate.getTags();
		assertEquals(6, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		verifyTag(cursoredList.iterator().next());
	}

	@Test
	public void testGetTagsPagination() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/tags.json?count=20&page=5"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("cursoredFavoriteTags"),
								MediaType.APPLICATION_JSON));
		CursoredList<Tag> cursoredList = favoriteTemplate.getTags(20, 5);
		assertEquals(6, cursoredList.getTotalNumber());
		assertEquals(2, cursoredList.size());
		verifyTag(cursoredList.iterator().next());
	}

	@Test
	public void testUpdateTags() {
		mockServer
				.expect(requestTo("https://api.weibo.com/2/favorites/tags/update.json"))
				.andExpect(method(POST))
				.andExpect(content().string("id=1&tags=%E5%A5%BD%2C%E6%BC%82%E4%BA%AE"))
				.andExpect(header("Authorization", "OAuth2 accessToken"))
				.andRespond(
						withSuccess(jsonResource("favorite"), MediaType.APPLICATION_JSON));
		verifyFavorite(favoriteTemplate.updateTags(1, Arrays.asList("好", "漂亮")));
	}

	private void verifyFavorite(Favorite favorite) {
		List<Tag> tags = favorite.getTags();
		assertEquals(2, tags.size());
		Tag firstTag = tags.iterator().next();
		verifyTag(firstTag);
		assertEquals(1306998976000L, favorite.getFavoritedTime().getTime());
		StatusMatcher.verifyStatus(favorite.getStatus());
	}

	private void verifyTag(Tag firstTag) {
		assertEquals(23, firstTag.getId());
		assertEquals("80后", firstTag.getValue());
		assertEquals(25369, firstTag.getCount());
	}
}
