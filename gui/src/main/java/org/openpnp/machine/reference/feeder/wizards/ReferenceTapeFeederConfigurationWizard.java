/*
 	Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 	
 	This file is part of OpenPnP.
 	
	OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
 	
 	For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.machine.reference.feeder.wizards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.components.CameraView;
import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.components.LocationButtonsPanel;
import org.openpnp.gui.support.BufferedImageIconConverter;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.gui.support.JBindings;
import org.openpnp.gui.support.JBindings.WrappedBinding;
import org.openpnp.gui.support.LengthConverter;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.gui.support.ApplyResetBindingListener;
import org.openpnp.gui.support.Wizard;
import org.openpnp.gui.support.WizardContainer;
import org.openpnp.machine.reference.feeder.ReferenceTapeFeeder;
import org.openpnp.model.Configuration;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class ReferenceTapeFeederConfigurationWizard extends JPanel implements Wizard {
	private final ReferenceTapeFeeder feeder;
	
	private WizardContainer wizardContainer;

	private JTextField textFieldFeedStartX;
	private JTextField textFieldFeedStartY;
	private JTextField textFieldFeedStartZ;
	private JTextField textFieldFeedEndX;
	private JTextField textFieldFeedEndY;
	private JTextField textFieldFeedEndZ;
	private JTextField textFieldFeedRate;
	private JButton btnSave;
	private JButton btnCancel;
	private JLabel lblActuatorId;
	private JTextField textFieldActuatorId;
	private JPanel panelGeneral;
	private JPanel panelVision;
	private JPanel panelLocations;
	private JCheckBox chckbxVisionEnabled;
	private JPanel panelVisionEnabled;
	private JPanel panelTemplate;
	private JLabel labelTemplateImage;
	private JButton btnChangeTemplateImage;
	private JSeparator separator;
	private JPanel panelVisionTemplateAndAoe;
	private JPanel panelAoE;
	private JLabel lblX_1;
	private JLabel lblY_1;
	private JTextField textFieldAoiX;
	private JTextField textFieldAoiY;
	private JTextField textFieldAoiWidth;
	private JTextField textFieldAoiHeight;
	

	private List<WrappedBinding> wrappedBindings = new ArrayList<WrappedBinding>();

	public ReferenceTapeFeederConfigurationWizard(ReferenceTapeFeeder referenceTapeFeeder) {
		this.feeder = referenceTapeFeeder;

		setLayout(new BorderLayout());

		JPanel panelFields = new JPanel();
		panelFields.setLayout(new BoxLayout(panelFields, BoxLayout.Y_AXIS));

		panelGeneral = new JPanel();
		panelGeneral.setBorder(new TitledBorder(null, "General Settings",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panelFields.add(panelGeneral);
		panelGeneral.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblFeedRate = new JLabel("Feed Rate (units/min)");
		panelGeneral.add(lblFeedRate, "2, 2");

		textFieldFeedRate = new JTextField();
		panelGeneral.add(textFieldFeedRate, "4, 2");
		textFieldFeedRate.setColumns(5);

		lblActuatorId = new JLabel("Actuator Id");
		panelGeneral.add(lblActuatorId, "2, 4, right, default");

		textFieldActuatorId = new JTextField();
		panelGeneral.add(textFieldActuatorId, "4, 4");
		textFieldActuatorId.setColumns(5);

		JScrollPane scrollPane = new JScrollPane(panelFields);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Configuration.get().getVerticalScrollUnitIncrement());

		panelLocations = new JPanel();
		panelFields.add(panelLocations);
		panelLocations.setBorder(new TitledBorder(null, "Locations",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLocations.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblX = new JLabel("X");
		panelLocations.add(lblX, "4, 4");

		JLabel lblY = new JLabel("Y");
		panelLocations.add(lblY, "6, 4");

		JLabel lblZ = new JLabel("Z");
		panelLocations.add(lblZ, "8, 4");

		JLabel lblFeedStartLocation = new JLabel("Feed Start Location");
		panelLocations.add(lblFeedStartLocation, "2, 6, right, default");

		textFieldFeedStartX = new JTextField();
		panelLocations.add(textFieldFeedStartX, "4, 6");
		textFieldFeedStartX.setColumns(8);

		textFieldFeedStartY = new JTextField();
		panelLocations.add(textFieldFeedStartY, "6, 6");
		textFieldFeedStartY.setColumns(8);

		textFieldFeedStartZ = new JTextField();
		panelLocations.add(textFieldFeedStartZ, "8, 6");
		textFieldFeedStartZ.setColumns(8);
		
		locationButtonsPanelFeedStart = new LocationButtonsPanel(textFieldFeedStartX, textFieldFeedStartY, textFieldFeedStartZ, null);
		panelLocations.add(locationButtonsPanelFeedStart, "10, 6");

		JLabel lblFeedEndLocation = new JLabel("Feed End Location");
		panelLocations.add(lblFeedEndLocation, "2, 8, right, default");

		textFieldFeedEndX = new JTextField();
		panelLocations.add(textFieldFeedEndX, "4, 8");
		textFieldFeedEndX.setColumns(8);

		textFieldFeedEndY = new JTextField();
		panelLocations.add(textFieldFeedEndY, "6, 8");
		textFieldFeedEndY.setColumns(8);

		textFieldFeedEndZ = new JTextField();
		panelLocations.add(textFieldFeedEndZ, "8, 8");
		textFieldFeedEndZ.setColumns(8);
		
		locationButtonsPanelFeedEnd = new LocationButtonsPanel(textFieldFeedEndX, textFieldFeedEndY, textFieldFeedEndZ, null);
		panelLocations.add(locationButtonsPanelFeedEnd, "10, 8");

		panelVision = new JPanel();
		panelVision.setBorder(new TitledBorder(null, "Vision",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelFields.add(panelVision);
		panelVision.setLayout(new BoxLayout(panelVision, BoxLayout.Y_AXIS));

		panelVisionEnabled = new JPanel();
		FlowLayout fl_panelVisionEnabled = (FlowLayout) panelVisionEnabled
				.getLayout();
		fl_panelVisionEnabled.setAlignment(FlowLayout.LEFT);
		panelVision.add(panelVisionEnabled);

		chckbxVisionEnabled = new JCheckBox("Vision Enabled?");
		panelVisionEnabled.add(chckbxVisionEnabled);

		separator = new JSeparator();
		panelVision.add(separator);
		
		panelVisionTemplateAndAoe = new JPanel();
		panelVision.add(panelVisionTemplateAndAoe);
		panelVisionTemplateAndAoe.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		panelTemplate = new JPanel();
		panelTemplate.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Template Image", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelVisionTemplateAndAoe.add(panelTemplate, "2, 2, center, fill");
		panelTemplate.setLayout(new BoxLayout(panelTemplate, BoxLayout.Y_AXIS));
								
										labelTemplateImage = new JLabel("");
										labelTemplateImage.setAlignmentX(Component.CENTER_ALIGNMENT);
										panelTemplate.add(labelTemplateImage);
										labelTemplateImage.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
												null, null, null));
										labelTemplateImage.setMinimumSize(new Dimension(150, 150));
										labelTemplateImage.setMaximumSize(new Dimension(150, 150));
										labelTemplateImage.setHorizontalAlignment(SwingConstants.CENTER);
										labelTemplateImage.setSize(new Dimension(150, 150));
										labelTemplateImage.setPreferredSize(new Dimension(150, 150));
												
												panel = new JPanel();
												panelTemplate.add(panel);
												
														btnChangeTemplateImage = new JButton(selectTemplateImageAction);
														panel.add(btnChangeTemplateImage);
														btnChangeTemplateImage.setAlignmentX(Component.CENTER_ALIGNMENT);
														
														btnCancelChangeTemplateImage = new JButton(cancelSelectTemplateImageAction);
														panel.add(btnCancelChangeTemplateImage);
												
												panelAoE = new JPanel();
												panelAoE.setBorder(new TitledBorder(null, "Area of Interest", TitledBorder.LEADING, TitledBorder.TOP, null, null));
												panelVisionTemplateAndAoe.add(panelAoE, "4, 2, fill, fill");
												panelAoE.setLayout(new FormLayout(new ColumnSpec[] {
														FormFactory.RELATED_GAP_COLSPEC,
														ColumnSpec.decode("default:grow"),
														FormFactory.RELATED_GAP_COLSPEC,
														ColumnSpec.decode("default:grow"),
														FormFactory.RELATED_GAP_COLSPEC,
														FormFactory.DEFAULT_COLSPEC,
														FormFactory.RELATED_GAP_COLSPEC,
														FormFactory.DEFAULT_COLSPEC,
														FormFactory.RELATED_GAP_COLSPEC,
														FormFactory.DEFAULT_COLSPEC,
														FormFactory.RELATED_GAP_COLSPEC,
														FormFactory.DEFAULT_COLSPEC,},
													new RowSpec[] {
														FormFactory.RELATED_GAP_ROWSPEC,
														FormFactory.DEFAULT_ROWSPEC,
														FormFactory.RELATED_GAP_ROWSPEC,
														FormFactory.DEFAULT_ROWSPEC,}));
												
												lblX_1 = new JLabel("X");
												panelAoE.add(lblX_1, "2, 2");
												
												lblY_1 = new JLabel("Y");
												panelAoE.add(lblY_1, "4, 2");
												
												lblWidth = new JLabel("Width");
												panelAoE.add(lblWidth, "6, 2");
												
												lblHeight = new JLabel("Height");
												panelAoE.add(lblHeight, "8, 2");
												
												textFieldAoiX = new JTextField();
												panelAoE.add(textFieldAoiX, "2, 4, fill, default");
												textFieldAoiX.setColumns(5);
												
												textFieldAoiY = new JTextField();
												panelAoE.add(textFieldAoiY, "4, 4, fill, default");
												textFieldAoiY.setColumns(5);
												
												textFieldAoiWidth = new JTextField();
												panelAoE.add(textFieldAoiWidth, "6, 4, fill, default");
												textFieldAoiWidth.setColumns(5);
												
												textFieldAoiHeight = new JTextField();
												panelAoE.add(textFieldAoiHeight, "8, 4, fill, default");
												textFieldAoiHeight.setColumns(5);
												
												btnChangeAoi = new JButton("Change");
												btnChangeAoi.setAction(selectAoiAction);
												panelAoE.add(btnChangeAoi, "10, 4");
												
												btnCancelChangeAoi = new JButton("Cancel");
												btnCancelChangeAoi.setAction(cancelSelectAoiAction);
												panelAoE.add(btnCancelChangeAoi, "12, 4");
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panelActions = new JPanel();
		panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(panelActions, BorderLayout.SOUTH);

		btnCancel = new JButton(cancelAction);
		panelActions.add(btnCancel);

		btnSave = new JButton(saveAction);
		panelActions.add(btnSave);
		
		cancelSelectTemplateImageAction.setEnabled(false);
		cancelSelectAoiAction.setEnabled(false);
		
		createBindings();
		loadFromModel();
	}

	private void createBindings() {
		LengthConverter lengthConverter = new LengthConverter(Configuration.get());
		IntegerConverter intConverter = new IntegerConverter();
		BufferedImageIconConverter imageConverter = new BufferedImageIconConverter();
		ApplyResetBindingListener listener = new ApplyResetBindingListener(saveAction, cancelAction);

		wrappedBindings.add(JBindings.bind(feeder, "feedRate", textFieldFeedRate,
				"text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "actuatorId",
				textFieldActuatorId, "text", listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthX",
				textFieldFeedStartX, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthY",
				textFieldFeedStartY, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedStartLocation.lengthZ",
				textFieldFeedStartZ, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthX",
				textFieldFeedEndX, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthY",
				textFieldFeedEndY, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "feedEndLocation.lengthZ",
				textFieldFeedEndZ, "text", lengthConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.enabled",
				chckbxVisionEnabled, "selected", listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.templateImage",
				labelTemplateImage, "icon", imageConverter, listener));
		
		wrappedBindings.add(JBindings.bind(feeder, "vision.areaOfInterest.x",
				textFieldAoiX, "text", intConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.areaOfInterest.y",
				textFieldAoiY, "text", intConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.areaOfInterest.width",
				textFieldAoiWidth, "text", intConverter, listener));
		wrappedBindings.add(JBindings.bind(feeder, "vision.areaOfInterest.height",
				textFieldAoiHeight, "text", intConverter, listener));
		
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedRate);
		ComponentDecorators.decorateWithAutoSelect(textFieldActuatorId);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedStartX);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedStartY);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedStartZ);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedEndX);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedEndY);
		ComponentDecorators.decorateWithAutoSelectAndLengthConversion(textFieldFeedEndZ);
		ComponentDecorators.decorateWithAutoSelect(textFieldAoiX);
		ComponentDecorators.decorateWithAutoSelect(textFieldAoiY);
		ComponentDecorators.decorateWithAutoSelect(textFieldAoiWidth);
		ComponentDecorators.decorateWithAutoSelect(textFieldAoiHeight);
		
		BeanProperty actuatorIdProperty = BeanProperty.create("actuatorId");
		Bindings.createAutoBinding(UpdateStrategy.READ, feeder, actuatorIdProperty, locationButtonsPanelFeedStart, actuatorIdProperty).bind();
		Bindings.createAutoBinding(UpdateStrategy.READ, feeder, actuatorIdProperty, locationButtonsPanelFeedEnd, actuatorIdProperty).bind();
	}

	private void loadFromModel() {
		for (WrappedBinding wrappedBinding : wrappedBindings) {
			wrappedBinding.reset();
		}
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
	}

	private void saveToModel() {
		for (WrappedBinding wrappedBinding : wrappedBindings) {
			wrappedBinding.save();
		}
		saveAction.setEnabled(false);
		cancelAction.setEnabled(false);
	}

	@Override
	public void setWizardContainer(WizardContainer wizardContainer) {
		this.wizardContainer = wizardContainer;
	}

	@Override
	public JPanel getWizardPanel() {
		return this;
	}

	@Override
	public String getWizardName() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("serial")
	private Action saveAction = new AbstractAction("Apply") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveToModel();
			wizardContainer
					.wizardCompleted(ReferenceTapeFeederConfigurationWizard.this);
			btnChangeTemplateImage.setAction(selectTemplateImageAction);
		}
	};

	@SuppressWarnings("serial")
	private Action cancelAction = new AbstractAction("Reset") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			loadFromModel();
			btnChangeTemplateImage.setAction(selectTemplateImageAction);
		}
	};

	@SuppressWarnings("serial")
	private Action selectTemplateImageAction = new AbstractAction(
			"Select") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView(); 
			cameraView.setSelectionEnabled(true);
//			org.openpnp.model.Rectangle r = feeder.getVision().getTemplateImageCoordinates();
			org.openpnp.model.Rectangle r = null;
			if (r == null || r.getWidth() == 0 || r.getHeight() == 0) {
				cameraView.setSelection(0, 0, 100, 100);
			}
			else {
//				cameraView.setSelection(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
			}
			btnChangeTemplateImage.setAction(confirmSelectTemplateImageAction);
			cancelSelectTemplateImageAction.setEnabled(true);
		}
	};
	
	@SuppressWarnings("serial")
	private Action confirmSelectTemplateImageAction = new AbstractAction(
			"Confirm") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			new Thread() {
				public void run() {
					CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView(); 
					BufferedImage image = cameraView.captureSelectionImage();
					if (image == null) {
						MessageBoxes.errorBox(
								ReferenceTapeFeederConfigurationWizard.this, 
								"No Image Selected", 
								"Please select an area of the camera image using the mouse.");
					}
					else {
						labelTemplateImage.setIcon(new ImageIcon(image));
					}
					cameraView.setSelectionEnabled(false);
					btnChangeTemplateImage.setAction(selectTemplateImageAction);
					cancelSelectTemplateImageAction.setEnabled(false);
				}
			}.start();
		}
	};
	
	@SuppressWarnings("serial")
	private Action cancelSelectTemplateImageAction = new AbstractAction(
			"Cancel") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			btnChangeTemplateImage.setAction(selectTemplateImageAction);
			cancelSelectTemplateImageAction.setEnabled(false);
			CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView();
			if (cameraView == null) {
				MessageBoxes.errorBox(getTopLevelAncestor(), "Error", "Unable to locate Camera.");
			}
			cameraView.setSelectionEnabled(false);
		}
	};
	
	@SuppressWarnings("serial")
	private Action selectAoiAction = new AbstractAction(
			"Select") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			btnChangeAoi.setAction(confirmSelectAoiAction);
			cancelSelectAoiAction.setEnabled(true);

			CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView(); 
			cameraView.setSelectionEnabled(true);
			org.openpnp.model.Rectangle r = feeder.getVision().getAreaOfInterest();
			if (r == null || r.getWidth() == 0 || r.getHeight() == 0) {
				cameraView.setSelection(0, 0, 100, 100);
			}
			else {
				cameraView.setSelection(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			}
		}
	};
	
	@SuppressWarnings("serial")
	private Action confirmSelectAoiAction = new AbstractAction(
			"Confirm") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			new Thread() {
				public void run() {
					btnChangeAoi.setAction(selectAoiAction);
					cancelSelectAoiAction.setEnabled(false);

					CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView(); 
					cameraView.setSelectionEnabled(false);
					final Rectangle rect = cameraView.getSelection();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textFieldAoiX.setText(Integer.toString(rect.x));
							textFieldAoiY.setText(Integer.toString(rect.y));
							textFieldAoiWidth.setText(Integer.toString(rect.width));
							textFieldAoiHeight.setText(Integer.toString(rect.height));
						}
					});
				}
			}.start();
		}
	};
	
	@SuppressWarnings("serial")
	private Action cancelSelectAoiAction = new AbstractAction(
			"Cancel") {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			btnChangeAoi.setAction(selectAoiAction);
			cancelSelectAoiAction.setEnabled(false);
			CameraView cameraView = MainFrame.cameraPanel.getSelectedCameraView();
			if (cameraView == null) {
				MessageBoxes.errorBox(getTopLevelAncestor(), "Error", "Unable to locate Camera.");
			}
			cameraView.setSelectionEnabled(false);
		}
	};
	
	
	private LocationButtonsPanel locationButtonsPanelFeedStart;
	private LocationButtonsPanel locationButtonsPanelFeedEnd;
	private JLabel lblWidth;
	private JLabel lblHeight;
	private JButton btnChangeAoi;
	private JButton btnCancelChangeAoi;
	private JPanel panel;
	private JButton btnCancelChangeTemplateImage;
}