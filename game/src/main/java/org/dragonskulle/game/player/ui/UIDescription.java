/* (C) 2021 DragonSkulle */
package org.dragonskulle.game.player.ui;

import org.dragonskulle.components.Component;
import org.dragonskulle.components.IFixedUpdate;
import org.dragonskulle.components.IOnAwake;
import org.dragonskulle.core.GameObject;
import org.dragonskulle.core.Reference;
import org.dragonskulle.game.player.BuildingDescriptor;
import org.dragonskulle.game.player.PredefinedBuildings;
import org.dragonskulle.ui.TransformUI;
import org.dragonskulle.ui.UIText;
import org.dragonskulle.ui.UITextRect;
import org.dragonskulle.utils.TextUtils;

/**
 * Used to display a {@link BuildingDescriptor}.
 *
 * @author Craig Wilbourne
 */
public class UIDescription extends Component implements IOnAwake, IFixedUpdate {

    // The different fields that are displayed:
    private Reference<UITextRect> mNameRef;
    private Reference<UITextRect> mAttackRef;
    private Reference<UITextRect> mDefenceRef;
    private Reference<UITextRect> mTokenRef;
    private Reference<UITextRect> mCostRef;

    /** Whether the component has been initialised. */
    private boolean initialised = false;

    @Override
    public void onAwake() {
        mNameRef = generateField(0.05f, -0.37f, 1f - 0.05f, -0.37f + 0.1f);
        mAttackRef = generateField(0.05f, -0.25f, 1f - 0.05f, -0.25f + 0.1f);
        mDefenceRef = generateField(0.05f, -0.15f, 1f - 0.05f, -0.15f + 0.1f);
        mTokenRef = generateField(0.05f, -0.05f, 1f - 0.05f, -0.05f + 0.1f);
        mCostRef = generateField(0.05f, 0.07f, 1f - 0.05f, 0.07f + 0.1f);
    }

    /**
     * Generate a new {@link GameObject}, and add a new {@link UITextRect} component.
     *
     * <p>Set the parent anchor to the specified positions.
     *
     * @param x The x position.
     * @param y The y position.
     * @param z The x position.
     * @param w The w position.
     * @return A {@link Reference} to the UITextRect.
     */
    private Reference<UITextRect> generateField(float x, float y, float z, float w) {
        UITextRect component = new UITextRect("");

        GameObject object = new GameObject("description_field", new TransformUI());
        object.addComponent(component);
        getGameObject().addChild(object);

        TransformUI transform = object.getTransform(TransformUI.class);
        transform.setParentAnchor(x, y, z, w);

        return component.getReference(UITextRect.class);
    }

    private void updateField(Reference<UITextRect> box, String text) {
        if (!Reference.isValid(box)) return;

        Reference<UIText> label = box.get().getLabelText();
        if (!Reference.isValid(label)) return;

        label.get().setText(text);

        System.out.println("ran: " + initialised);

        // The initial label text has been set.
        initialised = true;
    }

    /**
     * Update the description to match the desired descriptor.
     *
     * @param descriptor
     */
    void update(BuildingDescriptor descriptor) {
        final int length = 27;

        updateField(mNameRef, descriptor.getName().toUpperCase());
        updateField(mAttackRef, TextUtils.constructField("Attack", descriptor.getAttack(), length));
        updateField(
                mDefenceRef, TextUtils.constructField("Defence", descriptor.getDefence(), length));
        updateField(
                mTokenRef,
                TextUtils.constructField(
                        "Generation", descriptor.getTokenGenerationLevel(), length));
        updateField(mCostRef, TextUtils.constructField("COST", descriptor.getCost(), length));
    }

    @Override
    public void fixedUpdate(float deltaTime) {
        if (initialised) return;
        update(PredefinedBuildings.BASE);
    }

    @Override
    protected void onDestroy() {}
}
