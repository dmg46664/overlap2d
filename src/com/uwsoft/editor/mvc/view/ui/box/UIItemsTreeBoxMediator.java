/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.uwsoft.editor.mvc.view.ui.box;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.utils.Array;
import com.puremvc.patterns.mediator.SimpleMediator;
import com.puremvc.patterns.observer.Notification;
import com.uwsoft.editor.gdx.sandbox.Sandbox;
import com.uwsoft.editor.mvc.Overlap2DFacade;
import com.uwsoft.editor.mvc.factory.ItemFactory;
import com.uwsoft.editor.mvc.proxy.SceneDataManager;
import com.uwsoft.editor.utils.runtime.EntityUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by sargis on 4/10/15.
 */
public class UIItemsTreeBoxMediator extends PanelMediator<UIItemsTreeBox> {
    private static final String TAG = UIItemsTreeBoxMediator.class.getCanonicalName();
    public static final String NAME = TAG;

    public UIItemsTreeBoxMediator() {
        super(NAME, new UIItemsTreeBox());
    }

    @Override
    public String[] listNotificationInterests() {
        String[] parentNotifications = super.listNotificationInterests();
        return Stream.of(parentNotifications, new String[]{
                SceneDataManager.SCENE_LOADED,
                ItemFactory.NEW_ITEM_ADDED,
                UIItemsTreeBox.ITEMS_SELECTED
        }).flatMap(Stream::of).toArray(String[]::new);
    }

    @Override
    public void handleNotification(Notification notification) {
        super.handleNotification(notification);
        Sandbox sandbox = Sandbox.getInstance();
        switch (notification.getName()) {
            case SceneDataManager.SCENE_LOADED:
            	Entity rootEntity = sandbox.getRootEntity();
                viewComponent.init(rootEntity);
                break;
            case ItemFactory.NEW_ITEM_ADDED:
                rootEntity = sandbox.getRootEntity();
                viewComponent.init(rootEntity);
                break;
            case UIItemsTreeBox.ITEMS_SELECTED:
                Selection<Tree.Node> selection = notification.getBody();
                Array<Tree.Node> nodes = selection.toArray();
                for (Tree.Node node : nodes) {
                    Integer entityId = (Integer) node.getObject();
                    Entity item = EntityUtils.getByUniqueId(entityId);
                    if (item != null) {
                        addSelectionAction(item);
                    }
                }

                break;
        }
    }

    private void addSelectionAction(Entity entity) {
        Set<Entity> items = new HashSet<>();
        items.add(entity);
        Overlap2DFacade.getInstance().sendNotification(Sandbox.ACTION_SET_SELECTION, items);
    }
}
