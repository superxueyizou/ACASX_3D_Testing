/*******************************************************************************
 *  Copyright (C) Xueyi Zou - All Rights Reserved
 *  Written by Xueyi Zou <xz972@york.ac.uk>, 2015
 *  You are free to use/modify/distribute this file for whatever purpose!
 *  -----------------------------------------------------------------------
 *  |THIS FILE IS DISTRIBUTED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 *  |WARRANTY. THE USER WILL USE IT AT HIS/HER OWN RISK. THE ORIGINAL
 *  |AUTHORS AND COPPELIA ROBOTICS GMBH WILL NOT BE LIABLE FOR DATA LOSS,
 *  |DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING OR
 *  |MISUSING THIS SOFTWARE.
 *  ------------------------------------------------------------------------
 *******************************************************************************/
package visualization.configuration;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class EncounterConfigurator extends JDialog  
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPanel = new JPanel();

	public EncounterConfigurator(String intruderAlias) 
	{
		final EncounterConfig encounterConfig = Configuration.getInstance().encountersConfig.get(intruderAlias);	
		
		this.setModal(true);
		this.setBounds(1240, 50, 340, 700);
				
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));		
		contentPanel.setLayout(null);
		
		JLabel lblTitle = new JLabel("Configuring "+intruderAlias);
		lblTitle.setBounds(10, 0, 240, 33);
		contentPanel.add(lblTitle);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(237, 5, 63, 23);		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		contentPanel.add(btnOk);
		
		{
			JPanel positionPanel = new JPanel();
			positionPanel.setBorder(new TitledBorder(null, "Position", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			positionPanel.setBounds(10, 26, 292, 172);
			contentPanel.add(positionPanel);
			positionPanel.setLayout(null);
			
			JLabel lblCAPY = new JLabel("Y");
			lblCAPY.setBounds(10, 21, 44, 15);
			positionPanel.add(lblCAPY);
			
			final JLabel CPAYLabel = new JLabel(""+encounterConfig.CPAY);
			CPAYLabel.setBounds(234, 21, 48, 15);
			positionPanel.add(CPAYLabel);
			
			JSlider CAPYSlider = new JSlider();
			CAPYSlider.setBounds(64, 21, 160, 16);
			positionPanel.add(CAPYSlider);
			CAPYSlider.setSnapToTicks(true);
			CAPYSlider.setPaintLabels(true);
			CAPYSlider.setMinimum(-100);
			CAPYSlider.setValue((int)(encounterConfig.CPAY));
			CAPYSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPAY = source.getValue();
					CPAYLabel.setText(""+encounterConfig.CPAY);
				}
			});
			
			JLabel lblCAPR = new JLabel("R");
			lblCAPR.setBounds(10, 42, 44, 15);
			positionPanel.add(lblCAPR);
			
			final JLabel CAPRLabel = new JLabel(""+encounterConfig.CPAR);
			CAPRLabel.setBounds(234, 42, 48, 15);
			positionPanel.add(CAPRLabel);
			
			JSlider CAPRSlider = new JSlider();
			CAPRSlider.setMaximum(500);
			CAPRSlider.setBounds(64, 42, 160, 16);
			positionPanel.add(CAPRSlider);
			CAPRSlider.setSnapToTicks(true);
			CAPRSlider.setPaintLabels(true);
			CAPRSlider.setValue((int)(encounterConfig.CPAR));
			CAPRSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPAR = source.getValue();
					CAPRLabel.setText(""+encounterConfig.CPAR);
				}
			});
			
			JLabel lblCAPTheta = new JLabel("Theta");
			lblCAPTheta.setBounds(10, 63, 44, 15);
			positionPanel.add(lblCAPTheta);
			
			final JLabel CAPThetaLabel = new JLabel(""+encounterConfig.CPATheta);
			CAPThetaLabel.setBounds(234, 63, 48, 15);
			positionPanel.add(CAPThetaLabel);
			
			JSlider CAPThetaSlider = new JSlider();
			CAPThetaSlider.setBounds(64, 63, 160, 16);
			positionPanel.add(CAPThetaSlider);
			CAPThetaSlider.setSnapToTicks(true);
			CAPThetaSlider.setPaintLabels(true);		
			CAPThetaSlider.setMaximum(180);
			CAPThetaSlider.setMinimum(-180);
			CAPThetaSlider.setValue((int)(encounterConfig.CPATheta));
			CAPThetaSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPATheta = source.getValue();
					CAPThetaLabel.setText(""+encounterConfig.CPATheta);
				}
			});	
			
			JLabel lblCAPVy = new JLabel("VY");
			lblCAPVy.setBounds(10, 84, 44, 15);
			positionPanel.add(lblCAPVy);
			
			final JLabel CAPVyLabel = new JLabel(""+encounterConfig.CPAVy);
			CAPVyLabel.setBounds(234, 84, 48, 15);
			positionPanel.add(CAPVyLabel);
		
			JSlider CAPVySlider = new JSlider();
			CAPVySlider.setBounds(64, 84, 160, 16);
			positionPanel.add(CAPVySlider);
			CAPVySlider.setSnapToTicks(true);
			CAPVySlider.setPaintLabels(true);		
			CAPVySlider.setMaximum(58);
			CAPVySlider.setMinimum(-67);
			CAPVySlider.setValue((int)(encounterConfig.CPAVy));
			CAPVySlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPAVy = source.getValue();
					CAPVyLabel.setText(""+encounterConfig.CPAVy);

				}
			});
			
			JLabel lblCAPGs = new JLabel("GS");
			lblCAPGs.setBounds(10, 105, 44, 15);
			positionPanel.add(lblCAPGs);
			
			final JLabel CAPGsLabel = new JLabel(""+encounterConfig.CPAGs);
			CAPGsLabel.setBounds(234, 105, 48, 15);
			positionPanel.add(CAPGsLabel);
			
			JSlider CAPGsSlider = new JSlider();
			CAPGsSlider.setMinimum(169);
			CAPGsSlider.setBounds(64, 105, 160, 16);			
			CAPGsSlider.setSnapToTicks(true);
			CAPGsSlider.setPaintLabels(true);		
			CAPGsSlider.setMaximum(304);
			CAPGsSlider.setValue((int)(encounterConfig.CPAGs));
			CAPGsSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPAGs = source.getValue();
					CAPGsLabel.setText(""+encounterConfig.CPAGs);
				}
			});
			positionPanel.add(CAPGsSlider);
			
			JLabel lblCAPBearing = new JLabel("Bearing");
			lblCAPBearing.setBounds(10, 126, 55, 15);
			positionPanel.add(lblCAPBearing);
			
			final JLabel CAPBearingLabel = new JLabel(""+encounterConfig.CPABearing);
			CAPBearingLabel.setBounds(234, 126, 48, 15);
			positionPanel.add(CAPBearingLabel);
		
			JSlider CAPBearingSlider = new JSlider();
			CAPBearingSlider.setBounds(64, 126, 160, 16);			
			CAPBearingSlider.setSnapToTicks(true);
			CAPBearingSlider.setPaintLabels(true);		
			CAPBearingSlider.setMaximum(180);
			CAPBearingSlider.setMinimum(-180);
			CAPBearingSlider.setValue((int)(encounterConfig.CPABearing));
			CAPBearingSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPABearing = source.getValue();
					CAPBearingLabel.setText(""+encounterConfig.CPABearing);

				}
			});
			positionPanel.add(CAPBearingSlider);
			
			JLabel lblCAPT = new JLabel("T");
			lblCAPT.setBounds(10, 147, 44, 15);
			positionPanel.add(lblCAPT);
			
			final JLabel CAPTLabel = new JLabel(""+encounterConfig.CPAT);
			CAPTLabel.setBounds(234, 147, 48, 15);
			positionPanel.add(CAPTLabel);
		
			JSlider CAPTSlider = new JSlider();
			CAPTSlider.setBounds(64, 147, 160, 16);			
			CAPTSlider.setSnapToTicks(true);
			CAPTSlider.setPaintLabels(true);		
			CAPTSlider.setMaximum(30);
			CAPTSlider.setMinimum(20);
			CAPTSlider.setValue((int)(encounterConfig.CPAT));
			CAPTSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					encounterConfig.CPAT = source.getValue();
					CAPTLabel.setText(""+encounterConfig.CPAT);

				}
			});
			positionPanel.add(CAPTSlider);
		}
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		this.setVisible(true);

	}
}
